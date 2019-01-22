package com.battlezone.megamachines.renderer.game;

class GLShaderException extends RuntimeException {

    /**
     * To throw when an openGL shader encounters an error
     * @param errorMessage openGL error message
     */
    GLShaderException(String errorMessage) {
        super(errorMessage);
    }
}
