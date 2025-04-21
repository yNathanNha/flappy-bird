package me.nathan;

import me.nathan.graphics.Shader;
import me.nathan.input.Input;
import me.nathan.level.Level;
import me.nathan.math.Matrix4f;
import me.nathan.math.Vector3f;
import me.nathan.util.ShaderUtils;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    // The window handle
    private long window;

    private int width = 1280;
    private int height = 720;
    private String title = "Flappy";

    private Level level;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);


        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {

        GLFWErrorCallback.createPrint(System.err).set();


        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");


        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);
        });


        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*


            glfwGetWindowSize(window, pWidth, pHeight);


            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        Input.init(window);
        glfwMakeContextCurrent(window);

        glfwSwapInterval(0);

        glfwShowWindow(window);

        GL.createCapabilities();

        glClearColor(0f,0f,0f,0f);
        glEnable(GL_DEPTH_TEST);
        glActiveTexture(GL_TEXTURE1);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        Shader.loadAll();

        Matrix4f pr_matrix = Matrix4f.orthographic(-10.0f, 10.0f, -10.0f*9.0f/16.0f, 10.0f*9.0f/16.0f, -1.0f,1.0f);
        Shader.BG.setUniforMat4f("pr_matrix", pr_matrix);
        Shader.BG.setUniform1i("tex", 1);

        Shader.BIRD.setUniforMat4f("pr_matrix", pr_matrix);
        Shader.BIRD.setUniform1i("tex", 1);

        Shader.PIPE.setUniforMat4f("pr_matrix", pr_matrix);
        Shader.PIPE.setUniform1i("tex", 1);

        level = new Level();
    }

    private void loop() {

        // Set the clear color
        //glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        long lastTime = System.nanoTime();
        double ns = 1000000000.0 / 60.0;
        double delta = 0.0;
        int updates = 0;
        int frames = 0;
        long timer = System.currentTimeMillis();
        while ( !glfwWindowShouldClose(window) ) {
            long now = System.nanoTime();
            delta+=(now-lastTime) / ns;
            lastTime = now;
            if(delta >= 1) {
                fixedUpdate();
                updates++;
                delta--;
            }
            update();
            render();
            frames++;
            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println(updates + " ups, " + frames + " fps");
                updates = 0;
                frames = 0;
            }
        }


    }

    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        level.render();
        glfwSwapBuffers(window);
    }

    public void update(){
        glfwPollEvents();
        //level.bird.update();
    }

    public void fixedUpdate() {
        level.update();
        if(level.isGameOver()) {
            level = new Level();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

}