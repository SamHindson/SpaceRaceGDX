attribute vec4 a_color;
attribute vec3 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform float lol;

varying vec4 v_color;
varying vec2 v_texCoord0;

void main() {
	v_color = a_color;
	v_texCoord0 = a_texCoord0 + vec2(1 - a_texCoord0.x, 0);
	gl_Position =  u_projTrans * vec4(a_position, 1.);
}