package gfx;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL32;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Mesh implements Cloneable {

    private int VAO;
    private int VBO;
    private int EBO;

    public String name;

    private List<Vertex> vertices;
    private List<Integer> indices;
    private Material material;

    public Mesh(String name, List<Vertex> vertices, List<Integer> indices, Material material) {
        this.vertices = new ArrayList<>(vertices);
        this.indices  = new ArrayList<>(indices);
        this.material = material;
        this.name     = name;

        setupMesh();
    }

    public void dispose() {
        GL32.glDeleteBuffers(VBO);
        GL32.glDeleteBuffers(EBO);
        GL32.glDeleteVertexArrays(VAO);
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

        GL32.glEnableVertexAttribArray(0);
        GL32.glVertexAttribPointer(0, 3, GL32.GL_FLOAT, false, 8 * (Float.SIZE / 8), 0);
        GL32.glEnableVertexAttribArray(1);
        GL32.glVertexAttribPointer(1, 3, GL32.GL_FLOAT, false, 8 * (Float.SIZE / 8), 3 * (Float.SIZE / 8));
        GL32.glEnableVertexAttribArray(2);
        GL32.glVertexAttribPointer(2, 2, GL32.GL_FLOAT, false, 8 * (Float.SIZE / 8), 6 * (Float.SIZE / 8));

        GL32.glBindVertexArray(0);
    }

    public void draw(Shader shader) {
        shader.setUniformVec3("material.diffuse", material.diffuseColor);
        shader.setUniformVec3("material.ambient", material.ambientColor);
        shader.setUniformVec3("material.specular", material.specularColor);
        shader.setUniformFloat("material.shniness", material.shininess);

        if(material.diffuseTexture != null) {
            GL32.glActiveTexture(GL32.GL_TEXTURE0);
            GL32.glBindTexture(GL32.GL_TEXTURE_2D, material.diffuseTexture.getId());
            shader.setUniformBoolean("isDiffuseTextureSet", 1);
        } else {
            shader.setUniformBoolean("isDiffuseTextureSet", 0);
        }
        if(material.specularTexture != null) {
            GL32.glActiveTexture(GL32.GL_TEXTURE1);
            GL32.glBindTexture(GL32.GL_TEXTURE_2D, material.specularTexture.getId());
        }
        if(material.normalTexture != null) {
            GL32.glActiveTexture(GL32.GL_TEXTURE2);
            GL32.glBindTexture(GL32.GL_TEXTURE_2D, material.normalTexture.getId());
        }

        GL32.glBindVertexArray(VAO);
        GL32.glDrawElements(GL32.GL_TRIANGLES, indices.size(), GL32.GL_UNSIGNED_INT, 0);
        GL32.glBindVertexArray(0);
    }
}
