package com.chaika.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.chaika.SearchActivity;
import com.chaika.estructuraDatos.api.Credentials;
import com.chaika.estructuraDatos.malAppInfo.MyAnimeList;
import com.chaika.estructuraDatos.search.AnimeSearch;
import com.chaika.interfaces.ApiResult;
import com.orhanobut.logger.Logger;

/**
 * Clase que sirve de configuración de la aplicación, aqui se declaran todo lo que puede ser suceptible de ser requerido en distintos puntos
 * de la aplicación, a la cual siempre se puede acceder através de un método estatico.
 *
 * Esto nos permite usar metodos que requieran una actividad o un contexto fuera de Actividades, por ejemplo acceder a la base de datos
 * desde cualquier punto.
 * Así mismo almacena el nombre y contraseña del usuario para su uso en las llamadas al API.
 *
 * Created by Gato on 15/05/2017.
 */

public class ApplicationConfig implements ApiResult {
    private static ApplicationConfig instance;
    private Activity activity;
    private Context context;

    private ApiResult apiResult;

    private String Username;
    private String password;

    /***
     * Singleton
     * @return instancia única
     */
    public static ApplicationConfig getInstance(){
        if (instance == null){
            instance = new ApplicationConfig();
        }
        return instance;
    }
    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ApiResult getApiResult() {
        return apiResult;
    }

    public void setApiResult(ApiResult apiResult) {
        this.apiResult = apiResult;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    /***
     *
     * respuestas del servidor
     *
     */

    @Override
    public void SuccessCall(MyAnimeList myAnimeList) {

    }

    @Override
    public void SuccessCall(Credentials credentials) {

    }

    @Override
    public void SuccessCall(AnimeSearch animeSearch) {
        Logger.d(animeSearch);

        Bundle resultados = new Bundle();
        resultados.putParcelable("results",animeSearch);//pasamos los resultados
        //cuando recibimos respuesta del servidor abrimos una nueva actividad
        ApplicationConfig.getInstance().getActivity().startActivity(
                new Intent(ApplicationConfig.getInstance().getActivity(),
                        SearchActivity.class)
                        .putExtras(resultados));


    }

    @Override
    public void genericResponse(String response) {

    }

    @Override
    public void ErrorCall(Throwable error, String data) {

    }
}//fin clase
