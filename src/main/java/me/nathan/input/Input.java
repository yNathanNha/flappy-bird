package me.nathan.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

public class Input {

    private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];

    public static void init(long window) {
        GLFW.glfwSetKeyCallback(window, (windowHandle, key, scancode, action, mods) -> {
            if (action == GLFW.GLFW_PRESS) {
                keys[key] = true;
                //System.out.println("Key Pressed: " + GLFW.glfwGetKeyName(key, scancode));
            } else if (action == GLFW.GLFW_RELEASE) {
                keys[key] = false;
            }
        });
    }

    public static boolean isKeyPressed(int key) {
        return keys[key];
    }
}
