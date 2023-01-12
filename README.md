# Procesador JavaScript-PDL

Autores: 
- Nihel Kella Bouziane
- Julio Manso Sánchez-Tornero 

## Uso ##

``` terminal java -jar JavaScript-PDL.jar <input.txt> ```

### Información de uso: ###

    * Para ejecutar el procesador a través del ejecutable:
        1. Situarse en la raiz del proyecto, donde se encuentra el archivo JavaScript-PDL.jar
        2. Ejecutar el mandato descrito en el apartado 'Uso', sustituyendo `<input.txt> ` por la ruta del archivo a procesar
    * Para ejecutar el procesador a través de Makefile (necesario instalar la librería choco):
        1. Situarse en la raiz del proyecto y crear un archivo input.txt
        2. Compilar:   `make compile`
        3. Ejecutar:  ` make run fich=input.txt `
    Una vez ejecutado el procesador, se generarán los ficheros tokens.txt, parse.txt, ts.txt y errors.txt en la raiz. Si no aparecen, refrescar la carpeta.

## Árbol de directorios ##

``` terminal
JavaScript/
├── src/
│   ├── AFD.java
│   ├── JavaScript.java (main)
│   ├── MTpair.java
│   ├── Reader.java
│   ├── Writer.java
│   └── Token.java
├── data/
│   └── test/
│       ├── correcta1/
│       │   ├── Area.js
│       │   ├── parse.txt
│       │   ├── tokens.txt
│       │   └── TS.txt
│       ├── correcta2/
│       │   ├── AreaTriangulo.js
│       │   ├── parse.txt
│       │   ├── tokens.txt
│       │   └── TS.txt
│       ├── correcta3/
│       │   ├── Panaderia.js
│       │   ├── parse.txt
│       │   ├── tokens.txt
│       │   └── TS.txt
│       ├── correcta4/
│       │   ├── Macedonia.js
│       │   ├── parse.txt
│       │   ├── tokens.txt
│       │   └── TS.txt
│       ├── correcta5/
│       │   ├── Factorial.js
│       │   ├── parse.txt
│       │   ├── tokens.txt
│       │   └── TS.txt
│       ├── errores1/
│       │   ├── Modulo.js
│       │   └── errores.txt
│       ├── errores2/
│       │   ├── MsgLargo.js
│       │   └── errores.txt
│       ├── errores3/
│       │   ├── Operacion.js
│       │   └── errores.txt
│       ├── errores4/
│       │   ├── Mensajeria.js
│       │   └── errores.txt
│       └── errores5/
│           ├── Fecha.js
│           └── errores.txt
├── doc/
│   ├── AccionesSemanticas.txt
│   ├── GramaticaAL.txt
│   ├── GramaticaAS.txt
│   ├── TiposASem.txt
│   └── Tokens.txt
├── JavaScript-PDL.jar 
├── Makefile
└── README.md
```
