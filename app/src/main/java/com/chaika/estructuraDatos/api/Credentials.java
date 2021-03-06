package com.chaika.estructuraDatos.api;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Objeto utilizado para recibir la respuesta del servidor en la llamada de verificar credenciales.
 *
 * Created by Gato on 16/04/2017.
 */
@Root(name = "user")
public class Credentials {
    @Element(name = "id")
    public long user_id;
    @Element(name = "username")
    public String user_name;

    public Credentials(long user_id, String user_name) {
        this.user_id = user_id;
        this.user_name = user_name;
    }
    public Credentials(){}

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "user_id=" + user_id +
                ", user_name='" + user_name + '\'' +
                '}';
    }
}//fin clase
