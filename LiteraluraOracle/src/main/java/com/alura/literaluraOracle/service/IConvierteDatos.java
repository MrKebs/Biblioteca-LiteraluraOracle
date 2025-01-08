package com.alura.literaluraOracle.service;

public interface IConvierteDatos {

    <T> T obtenerDatos(String json, Class<T> clase);

}
