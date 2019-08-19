# AplicacionFireStorage

Aplicación utilizando las tecnologías Firebase Database Realtime y Firebase Storage.
Utilizo Firebase Authenticaction para que cada usuario pueda ser registrado, además de que sus
datos se almacenan en el firebase realtime database, si no está registrado se nos otorga una 
pantalla de registro, introduciendo los datos requeridos, para poder iniciar sesión, despues ya
ingresado el usuario se nos presenta la pantalla principal
la cual nos pide abrir un archivo, se otorgan los permisos 
necesarios para que la aplicación acceda a las imagenes del dispositivo, elegimos una
y el en textview que se presenta con el nombre de la imagen seleccionada, y en el imageview
se muestra la imagen dentro de ese componente, al dar en el botón subir se nos moestrará 
un ProgressDialog y al finalizar la subida nos aparecerá un mensaje, si la imagen fue subida
con éxito, un Toast nos mostrará el mensaje de subida exitosa, de lo contrario, mostrará otro
mensaje indicando que la subida no fue exitosa más el mensaje de error. Falta aplicar el recuperar imágenes
de la cámara, implementar como subir diferentes tipos de archivos.
