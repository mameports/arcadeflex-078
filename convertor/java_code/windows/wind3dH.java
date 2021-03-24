//============================================================
//
//	wind3d.h - Win32 Direct3D 7 (with DirectDraw 7) code
//
//============================================================

#ifndef __WIN32_D3D__
#define __WIN32_D3D__

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package windows;

public class wind3dH
{
	
	
	//============================================================
	//	GLOBAL VARIABLES
	//============================================================
	
	
	extern UINT8 win_d3d_effects_swapxy;
	extern UINT8 win_d3d_effects_flipx;
	extern UINT8 win_d3d_effects_flipy;
	
	
	
	//============================================================
	//	PROTOTYPES
	//============================================================
	
	int win_d3d_init(int width, int height, int depth, int attributes, double aspect, const struct win_effect_data *effect);
	int win_d3d_draw(struct mame_bitmap *bitmap, const struct rectangle *bounds, void *vector_dirty_pixels, int update);
	
	
	
	#endif
}
