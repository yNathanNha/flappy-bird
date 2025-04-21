package me.nathan.level;

import me.nathan.graphics.Shader;
import me.nathan.graphics.Texture;
import me.nathan.graphics.VertexArray;
import me.nathan.input.Input;
import me.nathan.math.Matrix4f;
import me.nathan.math.Vector2f;
import me.nathan.math.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

import java.util.Random;

public class Level {

    private VertexArray background, fade;
    private Texture bgTexture;

    private int xScroll = 0;
    private int map = 0;

    private Bird bird;

    private Pipe[] pipes = new Pipe[5*2];
    private Random rand = new Random();
    private float time = 0.0f;

    private int index = 0;

    private float OFFSET = 5.0f;

    private boolean control = true, reset = false;

    public Level() {
        float[] vertices = new float[] {
            -10.0f, -10*9.0f/16.0f, 0.0f,
            -10.0f, 10*9.0f/16.0f, 0.0f,
             0.0f,  10*9.0f/16.0f, 0.0f,
             0.0f, -10*9.0f/16.0f, 0.0f,
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

        fade = new VertexArray(6);
        background = new VertexArray(vertices, indices, tcs);
        bgTexture = new Texture("res/bg.png");

        bird = new Bird();
        generatePipes();

    }

    private void generatePipes() {
        Pipe.create();
        for(int i = 0 ; i < 5 * 2 ; i += 2) {
            pipes[i] = new Pipe(OFFSET + index * 3.0f, 7.0f + rand.nextFloat() * (13.0f - 7.0f));
            pipes[i + 1] = new Pipe(OFFSET + index * 3.0f - 1.5f, pipes[i].getY() - 18.5f);
            index +=2;
        }
    }

    private void updatePipes() {
        int modIndex = index % 10;  // Keep index within bounds

        pipes[modIndex] = new Pipe(OFFSET + index * 3.0f, 7.0f + rand.nextFloat() * (20.0f - 7.0f));
        pipes[(index + 1) % 10] = new Pipe(OFFSET + index * 3.0f - 1.5f, pipes[modIndex].getY() - 18.5f);

        index += 2;
    }


    public void update() {
        if(control) {
            xScroll--;

            if(-xScroll % 335 == 0) map++;
            if(-xScroll > 250 && -xScroll % 120 == 0) {
                updatePipes();
            }
        }

        bird.update(control);

        if(control && collision()) {
            bird.fall();
            control = false;

            System.out.println("Collision");
        }

        if(!control && Input.isKeyPressed(GLFW_KEY_SPACE))
            reset = true;

        time+=1/60f;

    }

    private void renderPipes() {
        Shader.PIPE.enable();
        Shader.PIPE.setUniforMat4f("vw_matrix", Matrix4f.translate(new Vector3f(xScroll*0.05f, 0.0f,0.0f)));
        Shader.PIPE.setUniform2f("bird", new Vector2f(0, bird.getY()));
        Pipe.getTexture().bind();
        Pipe.getMesh().bind();

        for (int i = 0; i <5 *2; i++) {
            Shader.PIPE.setUniforMat4f("ml_matrix",pipes[i].getModelMatrix());
            //Shader.PIPE.setUniform1i("top", i % 2 == 0 ? 1 : 0);
            if(i % 2 == 0){ Shader.PIPE.setUniforMat4f("ml_matrix",pipes[i].getModelMatrix().multiply(Matrix4f.rotate(180.0f)));}
            Pipe.getMesh().draw();
        }
        Pipe.getTexture().unbind();
        Pipe.getMesh().unbind();
    }

    private boolean collision() {
        for(int i = 0; i < 5*2; i++) {
            float bx = -xScroll* 0.05f;
            float by = bird.getY();
            float px = pipes[i].getX();
            float py = pipes[i].getY();

            if(i % 2 == 0) {
                px = pipes[i].getX() - 1.5f;
                py = pipes[i].getY() - 7.5f;
            }

            float bx0 = bx - bird.getSize() / 2.0f;
            float bx1 = bx + bird.getSize() / 2.0f;
            float by0 = by - bird.getSize() / 2.0f;
            float by1 = by + bird.getSize() / 2.0f;

            float px0 = px;
            float px1 = px + Pipe.getWidth();
            float py0 = py;
            float py1 = py + Pipe.getHeight();

            if(bx1 > px0 && bx0 < px1) {

                if(by1> py0 && by0 < py1) {
                    return true;
                }

            }
        }
        return false;
    }

    public boolean isGameOver() {
        return reset;
    }

    public void render() {
        bgTexture.bind();
        Shader.BG.enable();
        Shader.BG.setUniform2f("bird", new Vector2f(0, bird.getY()));
        background.bind();
        for(int i = map; i < map +4; i++){
            Shader.BG.setUniforMat4f("vw_matrix", Matrix4f.translate(new Vector3f(i *  10+xScroll*0.03f, 0.0f,0.0f)));
            background.draw();
        }
        Shader.BG.disable();
        bgTexture.unbind();

        renderPipes();
        bird.render();

        Shader.FADE.enable();
        Shader.FADE.setUniform1f("time", time);
        fade.render();
        Shader.FADE.disable();
    }

}
