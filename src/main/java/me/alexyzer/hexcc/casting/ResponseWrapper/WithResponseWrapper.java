package me.alexyzer.hexcc.casting.ResponseWrapper;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface WithResponseWrapper {
    CastResponseWrapper getResponseWrapper();
    default List<String> dumpReveal() {return getResponseWrapper().dumpReveal();}
    default @Nullable public String dumpMishap(){return getResponseWrapper().dumpMishap();}
}
