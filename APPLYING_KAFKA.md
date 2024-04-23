# [Pág. 41] Parte 2: Aplicando Kafka

# [Pág. 43] Diseño de un proyecto Kafka

---

En nuestro capítulo anterior, vimos cómo podemos trabajar con Kafka desde la línea de comandos y cómo usar un cliente Java. Ahora, ampliaremos esos primeros conceptos y analizaremos el diseño de varias soluciones con Kafka. Discutiremos algunas preguntas a considerar mientras diseñamos una estrategia para el proyecto de ejemplo que comenzaremos en este capítulo.

## [Pág. 44] Diseñar un proyecto kafka

Aunque las nuevas empresas y proyectos pueden utilizar Kafka al empezar, ese no es el caso para todos los que adoptan Kafka. Para aquellos de nosotros que hemos estado en entornos empresariales o trabajado con sistemas heredados, en realidad, comenzar desde cero no es un lujo que siempre tengamos. En este capítulo, trabajaremos en un proyecto para una empresa que está lista para cambiar su forma actual de manejar datos y aplicar este nuevo martillo llamado Kafka.

## [Pág. 44] Tomar el control de una arquitectura de datos existentes

Nuestra nueva empresa de consultoría ficticia acaba de ganar un contrato para ayudar a rediseñar una planta que funciona con bicicletas eléctricas y las gestiona de forma remota. Hay sensores colocados en toda la bicicleta que proporcionan continuamente eventos sobre la condición y el estado del equipo interno que están monitoreando. Sin embargo, se generan tantos eventos que el sistema actual ignora la mayoría de los mensajes. Se nos ha pedido que ayudemos a los propietarios del sitio a desbloquear el potencial de esos datos para que los utilicen sus diversas aplicaciones. Además de esto, nuestros datos actuales incluyen sistemas de bases de datos relacionales tradicionales que son grandes y agrupados. Con tantos sensores y una base de datos existente, **¿cómo podríamos crear nuestra nueva arquitectura basada en Kafka sin afectar la fabricación?**

## [Pág. 44] Un primer cambio

Una de las mejores formas de comenzar nuestra tarea probablemente no sea con un enfoque radical: no es necesario que todos nuestros datos se trasladen a Kafka a la vez. Si usamos una base de datos hoy y queremos aprovechar la transmisión de datos mañana, una de las rampas de acceso más fáciles comienza con Kafka Connect. Aunque puede manejar cargas de producción, no es necesario hacerlo desde el principio. Tomaremos una tabla de base de datos y comenzaremos nuestra nueva arquitectura mientras dejamos que las aplicaciones existentes se ejecuten por el momento. Pero primero, veamos algunos ejemplos para familiarizarnos con Kafka Connect.

## [Pág. 44] Funciones integradas

En nuestro ejemplo, **tomaremos datos de una fuente de datos y los colocaremos en Kafka para que podamos tratar los datos como si provinieran de un archivo Kafka.** Usando el archivo `connect-filesource.properties` como plantilla, que viene incluido en su instalación de Kafka `C:\kafka_2.13-3.7.0\config`, creemos un archivo llamado `alert-source.properties` y coloquemos las mismas propiedades que vienen en el archivo `connect-filesource.properties`.

Nuestro archivo definirá las configuraciones que necesitamos para configurar el archivo `alert.txt` y especificar los datos que se enviarán al `topic` específico `kinaction-alert-connect`.

Con configuraciones (y no código), podemos obtener datos en Kafka desde cualquier archivo. Dado que leer un archivo es una tarea común, podemos utilizar las clases prediseñadas de Connect. En este caso, la clase es `FileStreamSource`. Para el siguiente listado, supongamos que tenemos una aplicación que envía alertas a un archivo de texto.

```properties
# Archivo creado como parte del ejemplo del libro Kafka In Action 2020
# Desarrollador Martín
#
name=alert-source
connector.class=FileStreamSource
tasks.max=1
file=alert.txt
topic=kinaction-alert-connect
```

El valor de la propiedad `topic` es significativo. Lo usaremos más adelante para verificar que los mensajes se extraigan de un archivo al topic específico `kinaction-alert-connect`. El archivo `alert.txt` se monitorea para detectar cambios a medida que llegan nuevos mensajes. Y finalmente, elegimos `1` para el valor de `task.max` porque realmente solo necesitamos una tarea para nuestro conector y, en este ejemplo, no nos preocupa el paralelismo.

**NOTA** 
> Si está ejecutando ZooKeeper y Kafka localmente, asegúrese de tener sus propios agentes Kafka todavía ejecutándose como parte de este ejercicio (en caso de que los cierre después del capítulo anterior).

Ahora que hemos realizado la configuración necesaria, debemos `iniciar Connect` y enviar nuestras configuraciones. Podemos iniciar el proceso de conexión invocando el script de shell `connect-standalone.sh`, incluido nuestro archivo de configuración personalizado como parámetro de ese script. **Para iniciar Connect en una terminal, ejecute el comando en la siguiente lista y déjelo funcionando.*

```bash
C:\kafka_2.13-3.7.0
$ .\bin\windows\connect-standalone.bat .\config\connect-standalone.properties .\config\alert-source.properties

[2024-04-23 18:30:22,446] INFO Kafka Connect worker initializing ... (org.apache.kafka.connect.cli.AbstractConnectCli:114)
...
[2024-04-23 18:30:27,059] INFO [alert-source|task-0] Kafka version: 3.7.0 (org.apache.kafka.common.utils.AppInfoParser:124)
[2024-04-23 18:30:27,060] INFO [alert-source|task-0] Kafka commitId: 2ae524ed625438c5 (org.apache.kafka.common.utils.AppInfoParser:125)
[2024-04-23 18:30:27,061] INFO [alert-source|task-0] Kafka startTimeMs: 1713915027059 (org.apache.kafka.common.utils.AppInfoParser:126)
[2024-04-23 18:30:27,074] INFO [alert-source|task-0] [Producer clientId=connector-producer-alert-source-0] Cluster ID: 1ARk35xQSYKGDKNSEQt8BA (org.apache.kafka.clients.Metadata:349)
[2024-04-23 18:30:27,080] INFO [alert-source|task-0] AbstractConfig values:
        batch.size = 2000
        file = alert.txt
        topic = kinaction-alert-connect
 (org.apache.kafka.common.config.AbstractConfig:370)
[2024-04-23 18:30:27,080] INFO Created connector alert-source (org.apache.kafka.connect.cli.ConnectStandalone:87)
[2024-04-23 18:30:27,082] INFO [alert-source|task-0] WorkerSourceTask{id=alert-source-0} Source task finished initialization and start (org.apache.kafka.connect.runtime.AbstractWorkerSourceTask:281)
```

> Notar que el `.\config\alert-source.properties` es el archivo que creamos anterioremente.

Pasando a otra ventana de terminal, cree un archivo de texto llamado `alert.txt` en el directorio raíz de la instalación de Kafka y agregue un par de líneas de texto a este archivo usando su editor de texto; el texto puede ser lo que quiera. Ahora usemos el comando `console-consumer` para verificar que Connect esté haciendo su trabajo. Para eso, abriremos otra ventana de terminal y consumiremos del tema `kinaction-alert-connect`, usando la siguiente lista como ejemplo. Connect debería ingerir el contenido de este archivo `alert.txt` y producir los datos en Kafka.

```bash
C:\kafka_2.13-3.7.0
$ .\bin\windows\kafka-console-consumer.bat --topic kinaction-alert-connect --from-beginning --bootstrap-server localhost:9092
{"schema":{"type":"string","optional":false},"payload":"escribiendo desde notepad"}
```
