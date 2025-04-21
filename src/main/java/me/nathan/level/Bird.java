package me.nathan.level;

import me.nathan.graphics.Shader;
import me.nathan.graphics.Texture;
import me.nathan.graphics.VertexArray;
import me.nathan.input.Input;
import me.nathan.math.Matrix4f;
import me.nathan.math.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

public class Bird {

    private float SIZE = 1;
    private VertexArray mesh;
    private Texture texture;

    private Vector3f position = new Vector3f();

    private float rot;
    private float delta = 0.0f;

    public Bird() {
        float[] vertices = new float[] {
                -SIZE/2.0f, -SIZE/2.0f, 0.2f,
                -SIZE/2.0f, SIZE/2.0f, 0.2f,
                 SIZE/2.0f, SIZE/2.0f, 0.2f,
                 SIZE/2.0f, -SIZE/2.0f, 0.2f,
        };

        byte[] indices = new byte[] {
                0,1,2,
                2,3,0
        };

        float[] tcs = new float[] {
                0,1,
                0,0,
                1,0,
                1,1
        };

        mesh = new VertexArray(vertices, indices, tcs);
        texture = new Texture("res/bird.png");
    }

    public void update(boolean control) {
        position.y -= delta;
        if(Input.isKeyPressed(GLFW_KEY_SPACE) && control) {
            delta = -.10f;
        }else {
            delta += 0.01f;
        }

        rot = -delta * 90.0f;

    }

    public void fall(){
        delta = -.15f;
    }

    public void render() {
        Shader.BIRD.enable();
        Shader.BIRD.setUniforMat4f("ml_matrix", Matrix4f.translate(position).multiply(Matrix4f.rotate(rot)));
        texture.bind();
        mesh.render();
        Shader.BIRD.disable();
    }

    public float getY() {
        return position.y;
    }

    public float getSize() {

        return SIZE;

    }
}
