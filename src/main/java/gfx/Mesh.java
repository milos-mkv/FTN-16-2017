package gfx;

import utils.Disposable;
import lombok.Data;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL30C.*;

@Data
public class Mesh implements Disposable {

    private int vao;
    private int vbo;
    private int ebo;
    private int indicesSize;

    private String name;
    private Material material;

    public Mesh(String name, List<Vertex> vertices, List<Integer> indices, Material material) {
        this.material = material;
        this.name     = name;
        this.indicesSize = indices.size();

        setupMesh(vertices, indices);
    }

    @Override
    public void dispose() {
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
    }

    private void setupMesh(List<Vertex> vertices, List<Integer> indices) {
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ebo = glGenBuffers();

        glBindVertexArray(vao);

        FloatBuffer vert = MemoryUtil.memAllocFloat(vertices.size() * 8);
        for (Vertex tmp : vertices) {
            vert.put(tmp.getPosition().x).put(tmp.getPosition().y).put(tmp.getPosition().z)
                .put(tmp.getNormal().x).put(tmp.getNormal().y).put(tmp.getNormal().z)
                .put(tmp.getTexCoords().x).put(tmp.getTexCoords().y);
        }
        vert.flip();

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vert, GL_STATIC_DRAW);


        IntBuffer ind = MemoryUtil.memAllocInt(indices.size());
        for (Integer tmp : indices) {
            ind.put(tmp);
        }
        ind.flip();

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ind, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0L);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3L * 4);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6L * 4);

        glBindVertexArray(0);
    }

    public void draw(Shader shader) {

        shader.setUniformVec3("material.diffuse", material.getDiffuseColor());
        shader.setUniformVec3("material.ambient", material.getAmbientColor());
        shader.setUniformVec3("material.specular", material.getSpecularColor());
        shader.setUniformFloat("material.shniness", material.getShininess());

        if(material.getDiffuseTexture() != null) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, material.getDiffuseTexture().getId());
            shader.setUniformBoolean("isDiffuseTextureSet", 1);
        } else {
            shader.setUniformBoolean("isDiffuseTextureSet", 0);
        }
        if(material.getSpecularTexture() != null) {
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, material.getSpecularTexture().getId());
        }
        if(material.getNormalTexture() != null) {
            glActiveTexture(GL_TEXTURE2);
            glBindTexture(GL_TEXTURE_2D, material.getNormalTexture().getId());
        }

        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, indicesSize, GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }
}
