package com.ms.silverking.net.security;

import com.ms.silverking.text.ObjectDefParser2;

import java.net.Socket;

public class DummyAuthenticatorImpl2 extends Authenticator{
    static {
        ObjectDefParser2.addParser(new DummyAuthenticatorImpl2());
    }

    static String constructName() {
        return DummyAuthenticatorImpl2.class.getCanonicalName() + "()";
    }
    final static Authenticator.AuthFailedAction failedAction = Authenticator.AuthFailedAction.GO_WITHOUT_AUTH ;

    public DummyAuthenticatorImpl2() {
    }

    @Override
    public String getName() {
        return constructName();
    }

    @Override
    public Authenticator createLocalCopy() {
        return this;
    }

    @Override
    public Authenticator.AuthFailedAction onAuthTimeout(boolean serverside) {
        return Authenticator.AuthFailedAction.GO_WITHOUT_AUTH;
    }

    @Override
    public Authenticator.AuthResult syncAuthenticate(Socket unauthNetwork, boolean serverside, int timeoutInMillisecond) {
        return Authenticator.createAuthFailResult(failedAction);
    }
}
