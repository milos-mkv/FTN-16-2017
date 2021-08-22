package gfx;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joml.Vector3f;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class Light {

    protected Vector3f ambient;
    protected Vector3f diffuse;
    protected Vector3f specular;

}
