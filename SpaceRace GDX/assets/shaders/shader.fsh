varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_sampler2D;

//float4 outcolor = float4(0, 0, 0, 1);
//if (pp == 1) outcolor.r = color.r;
//    else if (pp == 2) outcolor.g = color.g;
//        else outcolor.b = color.b;
//return outcolor;

uniform float _VertsColor;
uniform float _VertsColor2;

uniform float f_ringy = 1;

void main() {
	int pp = (int)(1280 * v_texCoord0.x) % 3; 
	vec4 outcolor = vec4(0, 0, 0, 1);
	vec4 muls = vec4(1, 1, 1, 1);
	vec4 color = texture2D(u_sampler2D, v_texCoord0) * v_color * vec4(1, 1, 1, 1);
	
	float e = 0.3;
	
	if(pp == 1) {
		muls.r = 1; 
		muls.g = e;
	} else if(pp == 2) {
		muls.g = 1; 
		muls.b = e;
	} else {
        muls.b = 1; 
        muls.r = e;
    }
    
    outcolor = color * muls;

    outcolor.rgb *= f_ringy;
	
	gl_FragColor = outcolor;
}