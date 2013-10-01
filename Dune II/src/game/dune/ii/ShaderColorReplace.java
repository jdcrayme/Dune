package game.dune.ii;

import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.shader.exception.ShaderProgramException;
import org.andengine.opengl.shader.exception.ShaderProgramLinkException;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;
import org.andengine.util.color.Color;

import android.opengl.GLES20;

public class ShaderColorReplace extends ShaderProgram {
	 
    public static int sUniformModelViewPositionMatrixLocation = ShaderProgramConstants.LOCATION_INVALID;
    public static int sUniformTexture0Location = ShaderProgramConstants.LOCATION_INVALID;
    public static int sVaryinTextureCoordinates = ShaderProgramConstants.LOCATION_INVALID;
    public static int sUniformColorLocation = ShaderProgramConstants.LOCATION_INVALID;

	private Color color;
    
    public static final String DEFAULT_VERTEXSHADER = 
            "uniform mat4 " + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + ";\n" +
            "attribute vec4 " + ShaderProgramConstants.ATTRIBUTE_POSITION + ";\n" +
            "attribute vec2 " + ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES + ";\n" +
            "varying vec2 " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" +
            "void main() {\n" +
            "   " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + " = " + ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES + ";\n" +
            "   gl_Position = " + ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX + " * " + ShaderProgramConstants.ATTRIBUTE_POSITION + ";\n" +
            "}";
    
    public static final String DEFAULT_FRAGMENTSHADER = "precision mediump float;\n" +
            "uniform sampler2D " + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ";\n" +
            "uniform vec4 " + ShaderProgramConstants.UNIFORM_COLOR + ";\n" +
            "varying vec2 " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ";\n" +
            "void main() {\n" +
            "  vec4 color = texture2D(" + ShaderProgramConstants.UNIFORM_TEXTURE_0 + ", " + ShaderProgramConstants.VARYING_TEXTURECOORDINATES + ");\n" +
            "  float threshold = 0.2;\n" +
            "  float brightness; \n" +
            "  vec4 newColor = " + ShaderProgramConstants.UNIFORM_COLOR + ";\n" +
            "  if(color.r > threshold && color.b < threshold && color.g < threshold) { \n" +
            "  brightness = color.r - threshold;\n" +
            "  color.r = threshold;\n" +
            "  color += newColor*brightness;\n" + 
            "  }\n" +
            "  gl_FragColor = color;\n" +
            "}";

    ShaderColorReplace(Color color) {
        super(DEFAULT_VERTEXSHADER, DEFAULT_FRAGMENTSHADER);
        this.color = color;
    }

    @Override
    protected void link(final GLState pGLState) throws ShaderProgramLinkException {
        GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION, ShaderProgramConstants.ATTRIBUTE_POSITION);
        GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION, ShaderProgramConstants.ATTRIBUTE_COLOR);
        GLES20.glBindAttribLocation(this.mProgramID, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES);

        super.link(pGLState);

        ShaderColorReplace.sUniformModelViewPositionMatrixLocation = this.getUniformLocation(ShaderProgramConstants.UNIFORM_MODELVIEWPROJECTIONMATRIX);
        ShaderColorReplace.sUniformColorLocation = this.getUniformLocation(ShaderProgramConstants.UNIFORM_COLOR);
        ShaderColorReplace.sUniformTexture0Location = this.getUniformLocation(ShaderProgramConstants.UNIFORM_TEXTURE_0);
    }

    @Override
    public void bind(final GLState pGLState, final VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
        super.bind(pGLState, pVertexBufferObjectAttributes);

        setTexture(ShaderProgramConstants.UNIFORM_TEXTURE_0, 0);
        GLES20.glUniformMatrix4fv(ShaderColorReplace.sUniformModelViewPositionMatrixLocation, 1, false, pGLState.getModelViewProjectionGLMatrix(), 0);
        GLES20.glUniform4f(ShaderColorReplace.sUniformColorLocation, color.getRed(), color.getGreen(), color.getBlue(), 0);
    }

    @Override
    public void unbind(final GLState pGLState) throws ShaderProgramException {
        super.unbind(pGLState);
    }
}
