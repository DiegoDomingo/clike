Compilador clike_4.jar (V1.0)
Autores: Pablo Cervera López (815538)
         Diego Domingo Ralla (818637)
-------------------------------------------------------------
Análisis léxico, sintáctico y semántico

Invocar como:
-------------------------------------------------------------
java -jar clike_4.jar <fichero_fuente_clike>
-------------------------------------------------------------

Si se invoca sin parámetros, lee la entrada estándar.

Características generales:
1) No permite funciones anidadas (se ha comentado la parte que lo permite)
2) Usa LOOKAHEAD, aunque es fácilmente transformable en LL(1)
3) En el nivel más alto, se pueden intercalar declaraciones de
   funciones/procedimientos y declaraciones de variables. No así dentro de una
   función (ya que no hay anidamiento)
4) No se pueden intercalar instrucciones con declaraciones dentro de una
   func/proc: primero las declaraciones de variables, luego las instrucciones

Decisiones de diseño análisis sintáctico:
-------------------------------------------------------------
Se ha partido de la gramática parcial para clike proporcionada, que ha sido
adaptada añadiendo las producciones que se han considerado necesarias. Algunas
decisiones de diseño han sido:

- Se ha considerado que un asignable puede ser una variable escalar o una
  componente de un vector (indexada mediante una variable o mediante una
  constante entera).
- En toda función o procedimiento, después de la llave de apertura, la
  declaración de variables locales se hará lo primero.
- En las instrucciones de leer se ha considerado que entre los paréntesis habrá
  cero (si es lectura de una línea) o varios asignables, separados por comas. 
  Por otra parte, en las instrucciones de escribir se ha seguido el mismo 
  enfoque pero con expresiones en lugar de asignables.
- Se ha considerado que una instrucción if puede o no tener un bloque else, que
  se ha tratado con una producción específica para este.

Lenguaje aceptado semánticamente:
-------------------------------------------------------------
El lenguaje permite el uso de parámetros escalares y de vectores, tanto por
valor como por referencia en procedimientos y funciones.

En el directorio lib se han incluido las siguientes clases:
- Las relacionadas con la tabla de símbolos (SymbolTable), la estructura de
  datos donde se insertarán los distintos símbolos (Symbol) presentes en un
  programa. Se incluye una implementación de Symbol para cada tipo de dato 
  aceptado por el lenguaje clike.
- Clase Attributes, que recoge los distintos atributos asociados a un símbolo
  de una gramática.
- Clase SemanticFunctions, donde se recogen los distintos métodos y funciones de 
  las comprobaciones semánticas realizadas en cada una de las producciones de la
  gramática realizada.
- Clase ErrorSemantico, para mostrar los distintos errores semánticos que se 
  pueden producir.

Al ejecutar el programa clike se mostrará el contenido de la tabla de símbolos
al procesar cada uno de los procedimientos y funciones del programa.
