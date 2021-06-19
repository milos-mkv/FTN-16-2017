package gfx;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class Mesh extends TransformComponent {

    private int VAO, VBO, EBO;

    public String name;
    public Matrix4f getTransform() {
        return new Matrix4f().translate(position).rotate(rotation.x, 1, 0, 0)
                .rotate(rotation.y, 0, 1, 0).rotate(rotation.z, 0, 0, 1).scale(scale);
    }

    public ArrayList<Vertex> vertices;
    public ArrayList<Integer> indices;
    public Material material;

    public Mesh(String name, ArrayList<Vertex> vertices, ArrayList<Integer> indices, Material materials) {
        this.vertices = new ArrayList<>(vertices);
        this.indices = new ArrayList<>(indices);
        this.material = materials;
        this.name = name;

        setupMesh();
    }

    public void draw(Shader shader) {
        shader.setUniformMat4("model", getTransform());

        GL32.glBindVertexArray(VAO);
        GL32.glDrawElements(GL32.GL_TRIANGLES, indices.size(), GL32.GL_UNSIGNED_INT, 0);
        GL32.glBindVertexArray(0);
    }

    private void setupMesh() {
        VAO = GL32.glGenVertexArrays();
        VBO = GL32.glGenBuffers();
        EBO = GL32.glGenBuffers();

        GL32.glBindVertexArray(VAO);

        FloatBuffer vert = MemoryUtil.memAllocFloat(vertices.size() * 8);
        for (Vertex tmp : vertices) {
            vert.put(tmp.position.x).put(tmp.position.y).put(tmp.position.z)
                    .put(tmp.normal.x).put(tmp.normal.y).put(tmp.normal.z).put(tmp.texCoords.x).put(tmp.texCoords.y);
        }
        vert.flip();

        GL32.glBindBuffer(GL32.GL_ARRAY_BUFFER, VBO);
        GL32.glBufferData(GL32.GL_ARRAY_BUFFER, vert, GL32.GL_STATIC_DRAW);


        IntBuffer ind = MemoryUtil.memAllocInt(indices.size());
        for (Integer tmp : indices) {
            ind.put(tmp);
        }
        ind.flip();

        GL32.glBindBuffer(GL32.GL_ELEMENT_ARRAY_BUFFER, EBO);
        GL32.glBufferData(GL32.GL_ELEMENT_ARRAY_BUFFER, ind, GL32.GL_STATIC_DRAW);

        // vertex positions
        GL32.glEnableVertexAttribArray(0);
        GL32.glVertexAttribPointer(0, 3, GL32.GL_FLOAT, false, 8 * (Float.SIZE / 8), 0);
        // vertex normals
        GL32.glEnableVertexAttribArray(1);
        GL32.glVertexAttribPointer(1, 3, GL32.GL_FLOAT, false, 8 * (Float.SIZE / 8), 3 * (Float.SIZE / 8));
        // vertex texture coords
        GL32.glEnableVertexAttribArray(2);
        GL32.glVertexAttribPointer(2, 2, GL32.GL_FLOAT, false, 8 * (Float.SIZE / 8), 6 * (Float.SIZE / 8));

        GL32.glBindVertexArray(0);
    }
}
