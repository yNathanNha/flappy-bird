package me.nathan.graphics;

import me.nathan.math.Matrix4f;
import me.nathan.math.Vector2f;
import me.nathan.math.Vector3f;
import me.nathan.util.ShaderUtils;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    public static final int VERTEX_ATTRIB = 0;
    public static final int TCOORD_ATTRIB = 1;

    private int ID;
    private Map<String, Integer> locationCache = new HashMap<String, Integer>();

    public static Shader BASIC, BG, BIRD, PIPE, FADE;

    private boolean enabled = false;

    private Shader(String vertex, String fragment) {
        ID = ShaderUtils.load(vertex, fragment);
    }

    public static void loadAll() {
        BASIC = new Shader("shaders/shader.vert", "shaders/shader.frag");
        BG = new Shader("shaders/bg.vert", "shaders/bg.frag");
        BIRD = new Shader("shaders/bird.vert", "shaders/bird.frag");
        PIPE = new Shader("shaders/pipe.vert", "shaders/pipe.frag");
        FADE = new Shader("shaders/fade.vert","shaders/fade.frag");
    }

    public int getUniform(String name) {

        if(locationCache.containsKey(name)) {
            return locationCache.get(name);
        }

        int result = glGetUniformLocation(ID, name);

        if(result == -1) {
            System.err.println("Could not find the uniform " + name  + "!");
        }else {
            locationCache.put(name,result);
        }

        return result;
    }

    public void setUniform1i (String name, int value) {
        if(!enabled) enable();
        glUniform1i(getUniform(name), value);
    }

    public void setUniform1f (String name, float value) {
        if(!enabled) enable();
        glUniform1f(getUniform(name), value);
    }

    public void setUniform2f (String name, Vector2f vec2) {
        if(!enabled) enable();
        glUniform2f(getUniform(name), vec2.x, vec2.y);
    }

    public void setUniform3f (String name, Vector3f vec3) {
        if(!enabled) enable();
        glUniform3f(getUniform(name), vec3.x, vec3.y, vec3.z);
    }

    public void setUniforMat4f(String name, Matrix4f matrix) {
        if(!enabled) enable();
        glUniformMatrix4fv(getUniform(name), false, matrix.toFloatBuffer());
    }

    public void enable() {
        glUseProgram(ID);
        enabled = true;
    }

    public void disable() {
        glUseProgram(0);
        enabled = false;
    }

}
