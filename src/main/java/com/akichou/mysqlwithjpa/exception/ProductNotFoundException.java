package com.akichou.mysqlwithjpa.exception;

import java.text.MessageFormat;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String msg, Object... args) {

        super(MessageFormat.format(msg, args)) ;
    }
}
