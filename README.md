# Kafka en acción

Tomado del libro **Kafka In Action** de los autores **Dylan Scott, Viktor Gamov y Dave Klein**

## [Pág. 4] ¿Qué es Kafka?

El sitio web de `Apache Kafka` define Kafka como  una plataforma de streaming distribuido. Tiene tres capacidades principales.

- Lectura y escritura de registros como una cola de mensaje.
- Almacenamiento de registros con tolerancia a fallos.
- Procesamiento de flujos a medida que se producen.

Al igual que otras plataformas de mensajería, `Kafka actúa` (en términos reduccionistas) como un `intermediario` para los datos que entran en el sistema **(de los productores)** y salen del sistema **(para los consumidores o usuarios finales)**. El acoplamiento flexible se consigue permitiendo esta separación entre el productor y el usuario final del mensaje. `El productor puede enviar el mensaje que quiera sin saber si alguien está suscrito.` Además, `Kafka` tiene varias formas de entregar mensajes para adaptarse a su caso de negocio. La entrega de mensajes de Kafka puede adoptar al menos los siguientes tres métodos de entrega:

- `Semántica At-least-once (al menos una vez)`: se envía un mensaje según sea necesario hasta que se confirma.

- `Semántica At-most-once (como máximo una vez)`: un mensaje sólo se envía una vez y no se reenvía en caso de fallo.

- `Semántica Exactly-once (exactamente una vez)`: un mensaje sólo es visto una vez por el consumidor del mensaje. 

### Semántica At-least-once (al menos una vez)

Veamos qué significan estas opciones de mensajería. Veamos la semántica "al menos una vez" (figura 1.3). En este caso, Kafka puede configurarse para permitir a un productor de mensajes enviar el mismo mensaje más de una vez y que se escriba en los intermediarios. Si un mensaje no recibe la garantía de que ha sido escrito en el broker, el productor puede reenviar el mensaje [3]. Para aquellos casos en los que no se puede perder un mensaje, por ejemplo que alguien haya pagado una factura, esta garantía puede requerir algún filtro por parte del consumidor, pero es uno de los métodos de entrega más seguros.

![01.al-menos-una-vez](./assets/01.al-menos-una-vez.png)

### Semántica At-most-once (como máximo una vez)

La semántica At-most-once (figura 1.4) se da cuando un productor de mensajes puede enviar un mensaje una vez y no volver a intentarlo. En caso de fallo, el productor sigue adelante y no intenta enviarlo de nuevo [3]. ¿Por qué alguien estaría de acuerdo con perder un mensaje? Si un sitio web muy visitado realiza un seguimiento de las páginas vistas por sus visitantes, puede que le parezca bien perder algunos eventos de páginas vistas de los millones que procesa cada día. Mantener el sistema funcionando y no esperar reconocimientos puede compensar cualquier costo de pérdida de datos.

Kafka añadió la semántica exactly-once, también conocida como EOS, a su conjunto de características en la versión 0.11.0. EOS generó un gran debate con su lanzamiento [3]. Por un lado, la semántica "exactamente una vez" (figura 1.5) es ideal para muchos casos de uso. Parecía una garantía lógica para eliminar los mensajes duplicados, convirtiéndolos en cosa del pasado. Pero la mayoría de los desarrolladores aprecian enviar un mensaje y recibir ese mismo mensaje también en el lado del consumidor.

![como máximo una vez](./assets/02.como-maximo-una-vez.png)

### Semántica Exactly-once (exactamente una vez)

Otra discusión que siguió al lanzamiento de EOS fue un debate sobre si exactamente una vez era posible. Aunque esto entra en la teoría de la informática más profunda, es útil ser consciente de cómo Kafka define su función EOS [4]. Si un productor envía un mensaje más de una vez, sólo se entregará una vez al consumidor final. EOS tiene puntos de contacto en todas las capas de Kafka: productores, temas, corredores y consumidores.

Además de las diversas opciones de entrega, **otra ventaja habitual del gestor de mensajes es que, si la aplicación consumidora está inactiva por errores o mantenimiento, el productor no necesita esperar a que el consumidor gestione el mensaje. Cuando los consumidores vuelvan a estar en línea y procesen los datos, podrán continuar donde lo dejaron y no dejarán caer ningún mensaje.**

![exactamente una vez](./assets/03.exactamente-una-vez.png)