/***************************************************************************

	Tekunon Kougyou Beam Invader hardware

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class beaminv
{
	
	
	/*************************************
	 *
	 *	Memory handlers
	 *
	 *************************************/
	
	WRITE_HANDLER( beaminv_videoram_w )
	{
		UINT8 x,y;
		int i;
	
	
		videoram[offset] = data;
	
		y = ~(offset >> 8 << 3);
		x = offset;
	
		for (i = 0; i < 8; i++)
		{
			plot_pixel(tmpbitmap, x, y, data & 0x01);
	
			y--;
			data >>= 1;
		}
	}
}
