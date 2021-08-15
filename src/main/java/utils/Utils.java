package utils;

import org.joml.Matrix4f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public interface Utils {

    static String readFromFile(final String file) {
        StringBuilder stringBuilder = null;
        try (var bufferedReader = new BufferedReader(new FileReader(file))) {
            stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return stringBuilder != null ? stringBuilder.toString() : null;
    }

    static float[] matrix4x4ToFloatBuffer(Matrix4f matrix4f) {
        var buffer = new float[16];
        matrix4f.get(buffer);
        return buffer;
    }

    static float map(float value, float start1, float stop1, float start2, float stop2) {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    }

}
