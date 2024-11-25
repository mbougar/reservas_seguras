## EJERCICIO DE PRÁCTICA

Para poner en práctica la lección que estamos dando ahora mismo, vamos a realizar una serie de operaciones que 
nos aseguren que la aplicación funciona de forma correcta

1. **Cambiar el modo de generar el token asociado a la Session activa**

Ahora mismo estamos usando la clase UUID para generar un token aleatorio, pero vamos a cambiar esto para usar 
un cifrado simétrico a la hora de generar el token.   
Un cifrado simétrico de clave pública se basa en que se usa una misma "key" (clave pública) tanto para cifrar como para descifrar. 
La clave pública puede ser una cadena de caracteres cualquiera, que el algoritmo usará tanto para cifrar como para descifrar cualquier cadena. Veamos un ejemplo:
- clave pública: "clave_publica_que_no_cambia"
- mensaje a cifrar: "juanperez"
- Con la clave pública, aplicamos el algoritmo de cifrado al mensaje a cifrar. Resultado: "dCe91iouZuDe1Lud05p9T5dd3FF1"   

Usa un método de cifrado simétrico para poder generar el token. La cadena a cifrar debe ser: `nombre_usuario`

````kotlin
    /**
     * Método para cifrar una cadena usando un cifrado por clave simétrica
     * La key proporcionada por parámetros debe ser la misma que se vaya a usar a la hora de descifrar
     */
    fun encrypt(cadenaACifrar: String, key: String): String {
        // CON LA KEY PROPORCIONADA SE GENERA UNA CLAVE PÚBLICA
        val keyBytes = key.toByteArray(Charsets.UTF_8).copyOf(16) // AES necesita claves de 16, 24 o 32 bytes
        val secretKey = SecretKeySpec(keyBytes, "AES")

        // CON ESA CLAVE PÚBLICA SE PUEDE CIFRAR LA CADENA
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding") // Modo CBC con relleno PKCS5
        val iv = IvParameterSpec(ByteArray(16)) // Vector de inicialización fijo (NO recomendado para datos críticos)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
        val cipherText = cipher.doFinal(cadenaACifrar.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(cipherText)
    }

    /**
     * Método para descifrar una cadena usando un cifrado por clave simétrica
     * La key proporcionada por parámetros debe ser la misma que se usó para cifrar la cadena
     */
    fun decrypt(cadenaCifrada: String, key: String): String {

        // CON LA KEY PROPORCIONADA SE GENERA UNA CLAVE PÚBLICA
        val keyBytes = key.toByteArray(Charsets.UTF_8).copyOf(16) // AES necesita claves de 16, 24 o 32 bytes
        val secretKey = SecretKeySpec(keyBytes, "AES")

        // CON ESA CLAVE PÚBLICA SE PUEDE DESCIFRAR EL MENSAJE
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = IvParameterSpec(ByteArray(16)) // Mismo vector de inicialización que en el cifrado
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
        val plainText = cipher.doFinal(Base64.getDecoder().decode(cadenaCifrada))
        return String(plainText, Charsets.UTF_8)
    }
````

2. **Alta usuario y Cambiar el modo en el que se guarda la password en la base de datos**

Debemos implementar una nueva funcionalidad dentro de nuestra API que dé la posibilidad de registrar un nuevo usuario dentro de la base de datos.

``
localhost:8080/usuarios/alta
``

Cuando insertamos el usuario dentro de la base de datos, dicho usuario tiene que quedar almacenado junto con su password cifrada (puedes usar el mismo método que en el punto 1) o usar Bcript como Hash.

3. **Campo ROL para usuario**

Añade un campo extra a la entidad Usuario para almacenar el rol de dicho usuario. Habrá únicamente 2 roles permitidos, rol `USER` y rol `ADMIN`

4. **Lógica de Negocio para obtener e insertar nuevas reservas**

Como en este punto ya podemos cifrar y descifrar el token que nos llega en la Cookie, ya podemos saber quién está intentando realizar las operaciones de obtener reservas e insertar reservar. Realiza las siguientes configuraciones de seguridad:
- Para poder acceder a cualquiera de los endpoints de `ReservaController` el token debe ser válido (debe existir en la BDD y que la fecha de expiración sea superior a la fecha en la que se realiza la petición)
- Un usuario con `ROL USER` únicamente puede obtener sus propias reservas, es decir, si se intentan obtener las reservas del usuario "juanperez", el token debe pertenecer al usuario "juanperez". Lo mismo ocurre para insertar una reserva nueva
- Un usuario con `ROL ADMIN` puede obtener cualquier reserva de cualquier usuario y además puede insertar reservas nuevas para cualquier usuario
