/***************************************************************************

Senjyo / Star Force / Baluba-louk

driver by Mirko Buffoni

TODO:
- wrong background colors in baluba, intermissions after round 13


This board was obviously born to run Senjyo. Four scrolling layers, gradient
background, sprite/background priorities, and even a small bitmap for the
radar. Star Force uses only a small subset of the features.

MAIN BOARD:
0000-7fff ROM
8000-8fff RAM
9000-93ff Video RAM
9400-97ff Color RAM
9800-987f Sprites
9c00-9dff Palette RAM
a000-a37f Background Video RAM #3
a800-aaff Background Video RAM #2
b000-b1ff Background Video RAM #1
b800-bbff Radar bitmap

read:
d000      IN0
d001      IN1
d002      IN2
d003      ?
d004      DSW1
d005      DSW2

write:
9e20-9e21 background #1 x position
9e25      background #1 y position
9e28-9e29 background #? x position ??
9e30-9e31 background #2 & #3 x position
9e35      background #2 & #3 y position
d000      flip screen
d002      watchdog reset?
          IN0/IN1 latch ? ( write before read IN0/IN1 )
d004      sound command ( pio-a )

SOUND BOARD
memory read/write
0000-3fff ROM
4000-43ff RAM

write
8000 sound chip channel 1 1st 9f,bf,df,ff
9000 sound chip channel 2 1st 9f,bf,df,ff
a000 sound chip channel 3 1st 9f,bf,df,ff
d000 bit 0-3 single sound volume ( freq = ctc2 )
e000 ? ( initialize only )
f000 ? ( initialize only )

I/O read/write
00   z80pio-A data     ( from sound command )
01   z80pio-A controll ( mode 1 input )
02   z80pio-B data     ( no use )
03   z80pio-B controll ( mode 3 bit i/o )
08   z80ctc-ch1        ( timer mode cysclk/16, bas clock 15.625KHz )
09   z80ctc-ch2        ( cascade from ctc-1  , tempo interrupt 88.778Hz )
0a   z80ctc-ch3        ( timer mode , single sound freq. )
0b   z80ctc-ch4        ( no use )

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class senjyo
{
	
	
	
	extern UINT8 *senjyo_fgscroll;
	extern UINT8 *senjyo_scrollx1,*senjyo_scrolly1;
	extern UINT8 *senjyo_scrollx2,*senjyo_scrolly2;
	extern UINT8 *senjyo_scrollx3,*senjyo_scrolly3;
	extern UINT8 *senjyo_fgvideoram,*senjyo_fgcolorram;
	extern UINT8 *senjyo_bg1videoram,*senjyo_bg2videoram,*senjyo_bg3videoram;
	extern UINT8 *senjyo_radarram;
	extern UINT8 *senjyo_bgstripesram;
	
	DRIVER_INIT( starforc );
	DRIVER_INIT( starfore );
	DRIVER_INIT( senjyo );
	
	VIDEO_START( senjyo );
	VIDEO_UPDATE( senjyo );
	
	int senjyo_sh_start(const struct MachineSound *msound);
	
	
	
	#if 1
	#endif
	
	
	
	static int int_delay_kludge;
	
	MACHINE_INIT( senjyo )
	{
		/* we must avoid generating interrupts for the first few frames otherwise */
		/* Senjyo locks up. There must be an interrupt enable port somewhere, */
		/* or maybe interrupts are genenrated by the CTC. */
		/* Maybe a write to port d002 clears the IRQ line, but I'm not sure. */
		int_delay_kludge = 10;
	}
	
	INTERRUPT_GEN( senjyo_interrupt )
	{
		if (int_delay_kludge == 0) cpu_set_irq_line(0, 0, HOLD_LINE);
		else int_delay_kludge--;
	}
	
	public static WriteHandlerPtr flip_screen_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		flip_screen_set(data);
	} };
	
	
	
	public static Memory_ReadAddress readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x7fff, MRA_ROM ),
		new Memory_ReadAddress( 0x8000, 0x97ff, MRA_RAM ),
		new Memory_ReadAddress( 0x9800, 0x987f, MRA_RAM ),
		new Memory_ReadAddress( 0x9c00, 0x9d8f, MRA_RAM ),
		new Memory_ReadAddress( 0x9e00, 0x9e3f, MRA_RAM ),
		new Memory_ReadAddress( 0xa000, 0xa37f, MRA_RAM ),
		new Memory_ReadAddress( 0xa800, 0xaaff, MRA_RAM ),
		new Memory_ReadAddress( 0xb000, 0xb1ff, MRA_RAM ),
		new Memory_ReadAddress( 0xb800, 0xbbff, MRA_RAM ),
		new Memory_ReadAddress( 0xd000, 0xd000, input_port_0_r ),	/* player 1 input */
		new Memory_ReadAddress( 0xd001, 0xd001, input_port_1_r ),	/* player 2 input */
		new Memory_ReadAddress( 0xd002, 0xd002, input_port_2_r ),	/* coin */
		new Memory_ReadAddress( 0xd004, 0xd004, input_port_3_r ),	/* DSW1 */
		new Memory_ReadAddress( 0xd005, 0xd005, input_port_4_r ),	/* DSW2 */
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x7fff, MWA_ROM ),
		new Memory_WriteAddress( 0x8000, 0x8fff, MWA_RAM ),
		new Memory_WriteAddress( 0x9000, 0x93ff, senjyo_fgvideoram_w, senjyo_fgvideoram ),
		new Memory_WriteAddress( 0x9400, 0x97ff, senjyo_fgcolorram_w, senjyo_fgcolorram ),
		new Memory_WriteAddress( 0x9800, 0x987f, MWA_RAM, spriteram, spriteram_size ),
		new Memory_WriteAddress( 0x9c00, 0x9d8f, paletteram_IIBBGGRR_w, paletteram ),
		new Memory_WriteAddress( 0x9e00, 0x9e1f, MWA_RAM, senjyo_fgscroll ),
		new Memory_WriteAddress( 0x9e20, 0x9e21, MWA_RAM, senjyo_scrolly3 ),
	/*	new Memory_WriteAddress( 0x9e22, 0x9e23, height of the layer (Senjyo only, fixed at 0x380) */
		{ 0x9e25, 0x9e25, MWA_RAM, senjyo_scrollx3 },
		{ 0x9e27, 0x9e27, senjyo_bgstripes_w, senjyo_bgstripesram },	/* controls width of background stripes */
		{ 0x9e28, 0x9e29, MWA_RAM, senjyo_scrolly2 },
	/*	{ 0x9e2a, 0x9e2b, height of the layer (Senjyo only, fixed at 0x200) */
		{ 0x9e2d, 0x9e2d, MWA_RAM, senjyo_scrollx2 },
		{ 0x9e30, 0x9e31, MWA_RAM, senjyo_scrolly1 },
	/*	{ 0x9e32, 0x9e33, height of the layer (Senjyo only, fixed at 0x100) */
		{ 0x9e35, 0x9e35, MWA_RAM, senjyo_scrollx1 },
	/*	{ 0x9e38, 0x9e38, probably radar y position (Senjyo only, fixed at 0x61) */
	/*	{ 0x9e3d, 0x9e3d, probably radar x position (Senjyo only, 0x00/0xc0 depending on screen flip) */
	{ 0x9e00, 0x9e3f, MWA_RAM },
		{ 0xa000, 0xa37f, senjyo_bg3videoram_w, senjyo_bg3videoram },
		{ 0xa800, 0xaaff, senjyo_bg2videoram_w, senjyo_bg2videoram },
		{ 0xb000, 0xb1ff, senjyo_bg1videoram_w, senjyo_bg1videoram },
		{ 0xb800, 0xbbff, MWA_RAM, senjyo_radarram },
		{ 0xd000, 0xd000, flip_screen_w },
		{ 0xd004, 0xd004, z80pioA_0_p_w },
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	
	public static Memory_ReadAddress sound_readmem[]={
		new Memory_ReadAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_ReadAddress( 0x0000, 0x3fff, MRA_ROM ),
		new Memory_ReadAddress( 0x4000, 0x43ff, MRA_RAM ),
		new Memory_ReadAddress(MEMPORT_MARKER, 0)
	};
	
	public static Memory_WriteAddress sound_writemem[]={
		new Memory_WriteAddress(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8),
		new Memory_WriteAddress( 0x0000, 0x3fff, MWA_ROM ),
		new Memory_WriteAddress( 0x4000, 0x43ff, MWA_RAM ),
		new Memory_WriteAddress( 0x8000, 0x8000, SN76496_0_w ),
		new Memory_WriteAddress( 0x9000, 0x9000, SN76496_1_w ),
		new Memory_WriteAddress( 0xa000, 0xa000, SN76496_2_w ),
		new Memory_WriteAddress( 0xd000, 0xd000, senjyo_volume_w ),
	#if 0
		new Memory_WriteAddress( 0xe000, 0xe000, unknown ),
		new Memory_WriteAddress( 0xf000, 0xf000, unknown ),
	#endif
		new Memory_WriteAddress(MEMPORT_MARKER, 0)
	};
	
	public static IO_ReadPort sound_readport[]={
		new IO_ReadPort(MEMPORT_MARKER, MEMPORT_DIRECTION_READ | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_ReadPort( 0x00, 0x03, z80pio_0_r ),
		new IO_ReadPort( 0x08, 0x0b, z80ctc_0_r ),
		new IO_ReadPort(MEMPORT_MARKER, 0)
	};
	
	public static IO_WritePort sound_writeport[]={
		new IO_WritePort(MEMPORT_MARKER, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8),
		new IO_WritePort( 0x00, 0x03, z80pio_0_w ),
		new IO_WritePort( 0x08, 0x0b, z80ctc_0_w ),
		new IO_WritePort(MEMPORT_MARKER, 0)
	};
	
	
	
	
	static InputPortPtr input_ports_senjyo = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN2 */
	/* coin input for both must be active between 2 and 9 frames to be consistently recognized */
		PORT_BIT_IMPULSE( 0x01, IP_ACTIVE_HIGH, IPT_COIN1, 2 );
		PORT_BIT_IMPULSE( 0x02, IP_ACTIVE_HIGH, IPT_COIN2, 2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x01, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_3C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_6C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x30, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x30, "2" );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x10, "4" );
		PORT_DIPSETTING(    0x20, "5" );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x01, DEF_STR( "On") );
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x02, "100k only" );
		PORT_DIPSETTING(    0x00, "None" );
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x04, DEF_STR( "On") );
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x08, DEF_STR( "On") );
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x10, DEF_STR( "On") );
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x20, DEF_STR( "On") );
		PORT_DIPNAME( 0xc0, 0x40, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x80, "Easy" );
		PORT_DIPSETTING(    0x40, "Medium" );
		PORT_DIPSETTING(    0x00, "Hard" );
		PORT_DIPSETTING(    0xc0, "Hardest" );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_starforc = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN2 */
	/* coin input for both must be active between 2 and 9 frames to be consistently recognized */
		PORT_BIT_IMPULSE( 0x01, IP_ACTIVE_HIGH, IPT_COIN1, 2 );
		PORT_BIT_IMPULSE( 0x02, IP_ACTIVE_HIGH, IPT_COIN2, 2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x30, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x30, "2" );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x10, "4" );
		PORT_DIPSETTING(    0x20, "5" );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Demo_Sounds") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x07, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "50k, 200k and 500k" );
		PORT_DIPSETTING(    0x01, "100k, 300k and 800k" );
		PORT_DIPSETTING(    0x02, "50k and 200k" );
		PORT_DIPSETTING(    0x03, "100k and 300k" );
		PORT_DIPSETTING(    0x04, "50k only" );
		PORT_DIPSETTING(    0x05, "100k only" );
		PORT_DIPSETTING(    0x06, "200k only" );
		PORT_DIPSETTING(    0x07, "None" );
		PORT_DIPNAME( 0x38, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "Easiest" );
		PORT_DIPSETTING(    0x08, "Easy" );
		PORT_DIPSETTING(    0x10, "Medium" );
		PORT_DIPSETTING(    0x18, "Difficult" );
		PORT_DIPSETTING(    0x20, "Hard" );
		PORT_DIPSETTING(    0x28, "Hardest" );
		/* 0x30 and x038 are unused */
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	static InputPortPtr input_ports_baluba = new InputPortPtr(){ public void handler() { 
		PORT_START(); 	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN1 */
		PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN | IPF_8WAY | IPF_COCKTAIL );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* IN2 */
	/* coin input for both must be active between 2 and 9 frames to be consistently recognized */
		PORT_BIT_IMPULSE( 0x01, IP_ACTIVE_HIGH, IPT_COIN1, 2 );
		PORT_BIT_IMPULSE( 0x02, IP_ACTIVE_HIGH, IPT_COIN2, 2 );
		PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START1 );
		PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_START2 );
		PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN );
		PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN );
	
		PORT_START(); 	/* DSW0 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( "Coin_A") );
		PORT_DIPSETTING(    0x01, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x02, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x03, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x0c, 0x00, DEF_STR( "Coin_B") );
		PORT_DIPSETTING(    0x04, DEF_STR( "2C_1C") );
		PORT_DIPSETTING(    0x00, DEF_STR( "1C_1C") );
		PORT_DIPSETTING(    0x08, DEF_STR( "1C_2C") );
		PORT_DIPSETTING(    0x0c, DEF_STR( "1C_3C") );
		PORT_DIPNAME( 0x30, 0x00, DEF_STR( "Lives") );
		PORT_DIPSETTING(    0x30, "2" );
		PORT_DIPSETTING(    0x00, "3" );
		PORT_DIPSETTING(    0x10, "4" );
		PORT_DIPSETTING(    0x20, "5" );
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( "Cabinet") );
		PORT_DIPSETTING(    0x40, DEF_STR( "Upright") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Cocktail") );
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	
		PORT_START(); 	/* DSW1 */
		PORT_DIPNAME( 0x07, 0x00, DEF_STR( "Bonus_Life") );
		PORT_DIPSETTING(    0x00, "30k, 100k and 200k" );
		PORT_DIPSETTING(    0x01, "50k, 200k and 500k" );
		PORT_DIPSETTING(    0x02, "30k and 100k" );
		PORT_DIPSETTING(    0x03, "50k and 200k" );
		PORT_DIPSETTING(    0x04, "30k only" );
		PORT_DIPSETTING(    0x05, "100k only" );
		PORT_DIPSETTING(    0x06, "200k only" );
		PORT_DIPSETTING(    0x07, "None" );
		PORT_DIPNAME( 0x38, 0x00, DEF_STR( "Difficulty") );
		PORT_DIPSETTING(    0x00, "0" );
		PORT_DIPSETTING(    0x08, "1" );
		PORT_DIPSETTING(    0x10, "2" );
		PORT_DIPSETTING(    0x18, "3" );
		PORT_DIPSETTING(    0x20, "4" );
		PORT_DIPSETTING(    0x28, "5" );
		PORT_DIPSETTING(    0x30, "6" );
		PORT_DIPSETTING(    0x38, "7" );
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x40, DEF_STR( "On") );
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( "Unknown") );
		PORT_DIPSETTING(    0x00, DEF_STR( "Off") );
		PORT_DIPSETTING(    0x80, DEF_STR( "On") );
	INPUT_PORTS_END(); }}; 
	
	
	
	static GfxLayout charlayout = new GfxLayout
	(
		8,8,	/* 8*8 characters */
		512,	/* 512 characters */
		3,	/* 3 bits per pixel */
		new int[] { 0, 512*8*8, 2*512*8*8 },	/* the bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8 },
		8*8	/* every char takes 8 consecutive bytes */
	);
	static GfxLayout tilelayout_256 = new GfxLayout
	(
		16,16,	/* 16*16 characters */
		256,	/* 256 characters */
		3,	/* 3 bits per pixel */
		new int[] { 0, 256*16*16, 2*256*16*16 },	/* the bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
		32*8	/* every character takes 32 consecutive bytes */
	);
	static GfxLayout tilelayout_128 = new GfxLayout
	(
		16,16,	/* 16*16 characters */
		128,	/* 128 characters */
		3,	/* 3 bits per pixel */
		new int[] { 0, 128*16*16, 2*128*16*16 },	/* the bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
		32*8	/* every character takes 32 consecutive bytes */
	);
	static GfxLayout spritelayout1 = new GfxLayout
	(
		16,16,	/* 16*16 sprites */
		512,	/* 512 sprites */
		3,	/* 3 bits per pixel */
		new int[] { 0, 512*16*16, 2*512*16*16 },	/* the bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8 },
		32*8	/* every sprite takes 32 consecutive bytes */
	);
	static GfxLayout spritelayout2 = new GfxLayout
	(
		32,32,	/* 32*32 sprites */
		128,	/* 128 sprites */
		3,	/* 3 bits per pixel */
		new int[] { 0, 128*32*32, 2*128*32*32 },	/* the bitplanes are separated */
		new int[] { 0, 1, 2, 3, 4, 5, 6, 7,
				8*8+0, 8*8+1, 8*8+2, 8*8+3, 8*8+4, 8*8+5, 8*8+6, 8*8+7,
				32*8+0, 32*8+1, 32*8+2, 32*8+3, 32*8+4, 32*8+5, 32*8+6, 32*8+7,
				40*8+0, 40*8+1, 40*8+2, 40*8+3, 40*8+4, 40*8+5, 40*8+6, 40*8+7 },
		new int[] { 0*8, 1*8, 2*8, 3*8, 4*8, 5*8, 6*8, 7*8,
				16*8, 17*8, 18*8, 19*8, 20*8, 21*8, 22*8, 23*8,
				64*8, 65*8, 66*8, 67*8, 68*8, 69*8, 70*8, 71*8,
				80*8, 81*8, 82*8, 83*8, 84*8, 85*8, 86*8, 87*8 },
		128*8	/* every sprite takes 128 consecutive bytes */
	);
	
	static GfxDecodeInfo gfxdecodeinfo[] =
	{
		new GfxDecodeInfo( REGION_GFX1, 0, charlayout,       0, 8 ),	/*   0- 63 characters */
		new GfxDecodeInfo( REGION_GFX2, 0, tilelayout_256,  64, 8 ),	/*  64-127 background #1 */
		new GfxDecodeInfo( REGION_GFX3, 0, tilelayout_256, 128, 8 ),	/* 128-191 background #2 */
		new GfxDecodeInfo( REGION_GFX4, 0, tilelayout_128, 192, 8 ),	/* 192-255 background #3 */
		new GfxDecodeInfo( REGION_GFX5, 0, spritelayout1,  320, 8 ),	/* 320-383 normal sprites */
		new GfxDecodeInfo( REGION_GFX5, 0, spritelayout2,  320, 8 ),	/* 320-383 large sprites */
														/* 384-399 is background */
		new GfxDecodeInfo( -1 ) /* end of array */
	};
	
	
	
	static Z80_DaisyChain daisy_chain[] =
	{
		{ z80pio_reset , z80pio_interrupt, z80pio_reti , 0 }, /* device 0 = PIO_0 , low  priority */
		{ z80ctc_reset , z80ctc_interrupt, z80ctc_reti , 0 }, /* device 1 = CTC_0 , high priority */
		{ 0,0,0,-1} 	   /* end mark */
	};
	
	
	
	static SN76496interface sn76496_interface = new SN76496interface
	(
		3,	/* 3 chips */
		new int[] { 2000000, 2000000, 2000000 },	/* 2 MHz? */
		new int[] { 50, 50, 50 }
	);
	
	static CustomSound_interface custom_interface = new CustomSound_interface
	(
		senjyo_sh_start,
		senjyo_sh_stop,
		senjyo_sh_update
	);
	
	
	
	static MACHINE_DRIVER_START( senjyo )
	
		/* basic machine hardware */
		MDRV_CPU_ADD(Z80, 4000000)	/* 4 MHz? */
		MDRV_CPU_MEMORY(readmem,writemem)
		MDRV_CPU_VBLANK_INT(senjyo_interrupt,1)
	
		MDRV_CPU_ADD(Z80, 2000000)
		MDRV_CPU_CONFIG(daisy_chain)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 2 MHz? */
		MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
		MDRV_CPU_PORTS(sound_readport,sound_writeport)
	
		MDRV_FRAMES_PER_SECOND(60)
		MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)
	
		MDRV_MACHINE_INIT(senjyo)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
		MDRV_SCREEN_SIZE(32*8, 32*8)
		MDRV_VISIBLE_AREA(0*8, 32*8-1, 2*8, 30*8-1)
		MDRV_GFXDECODE(gfxdecodeinfo)
		MDRV_PALETTE_LENGTH(400+2)	/* 400 real palette + 2 for the radar */
	
		MDRV_VIDEO_START(senjyo)
		MDRV_VIDEO_UPDATE(senjyo)
	
		/* sound hardware */
		MDRV_SOUND_ADD(SN76496, sn76496_interface)
		MDRV_SOUND_ADD(CUSTOM, custom_interface)
	MACHINE_DRIVER_END
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	static RomLoadPtr rom_senjyo = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );    /* 64k for code */
		ROM_LOAD( "08m_05t.bin", 0x0000, 0x2000, CRC(b1f3544d);SHA1(59997164dfb740fce1862d89754be7517303161a) )
		ROM_LOAD( "08k_04t.bin", 0x2000, 0x2000, CRC(e34468a8);SHA1(1931788e4ebe0dab9525f795b639be6544a6b31a) )
		ROM_LOAD( "08j_03t.bin", 0x4000, 0x2000, CRC(c33aedee);SHA1(5adf83268ef7b91194dea41204bdb931a14f2158) )
		ROM_LOAD( "08f_02t.bin", 0x6000, 0x2000, CRC(0ef4db9e);SHA1(0dcb216495f4328c44cc0af87ffb0bd255b7dc1a) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );    /* 64k for sound board */
		ROM_LOAD( "02h_01t.bin", 0x0000, 0x2000, CRC(c1c24455);SHA1(24a2ab9e4df793f68f51bbe6a1313f38d951a8af) )
	
		ROM_REGION( 0x03000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "08h_08b.bin", 0x00000, 0x1000, CRC(0c875994);SHA1(6e4119ade0261eacf8349ff18f1cb7a50be2a9a4) )	/* fg */
		ROM_LOAD( "08f_07b.bin", 0x01000, 0x1000, CRC(497bea8e);SHA1(940592e04ef9dff0e410de040dafe4f6fc745070) )
		ROM_LOAD( "08d_06b.bin", 0x02000, 0x1000, CRC(4ef69b00);SHA1(bad4bbb7159a03efcc9dee1180c231c22bea8f47) )
	
		ROM_REGION( 0x06000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "05n_16m.bin", 0x00000, 0x1000, CRC(0d3e00fb);SHA1(da144da56733e13c754d066932a32eb6fcd9c83a) )	/* bg1 */
		ROM_LOAD( "05k_15m.bin", 0x02000, 0x1000, CRC(93442213);SHA1(01ceed1124022328b47607ee66d60fe06fdd46ea) )
		ROM_CONTINUE(			 0x04000, 0x1000             );
	
		ROM_REGION( 0x06000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "07n_18m.bin", 0x00000, 0x1000, CRC(d50fced3);SHA1(41f503b2d980548a564a414847b2b6c5ae71da2b) )	/* bg2 */
		ROM_LOAD( "07k_17m.bin", 0x02000, 0x1000, CRC(10c3a5f0);SHA1(ccf7e0b6686129afc6af542d20734e51702cd8a7) )
		ROM_CONTINUE(			 0x04000, 0x1000             );
	
		ROM_REGION( 0x03000, REGION_GFX4, ROMREGION_DISPOSE );
		ROM_LOAD( "09n_20m.bin", 0x00000, 0x1000, CRC(54cb8126);SHA1(f2d0b38d1c47a48240bc9e4bc962ef63f5c28ad6) )	/* bg3 */
		ROM_LOAD( "09k_19m.bin", 0x01000, 0x2000, CRC(373e047c);SHA1(0168e22ca72af515980c0975eb8525a416c6dd79) )
	
		ROM_REGION( 0x0c000, REGION_GFX5, ROMREGION_DISPOSE );
		ROM_LOAD( "08p_13b.bin", 0x00000, 0x2000, CRC(40127efd);SHA1(6f8c0f7e4658d54d8fcc6b6e6d2650483788eec1) )	/* sprites */
		ROM_LOAD( "08s_14b.bin", 0x02000, 0x2000, CRC(42648ffa);SHA1(61965428306f94c717b03208be9ac8c27265fcaa) )
		ROM_LOAD( "08m_11b.bin", 0x04000, 0x2000, CRC(ccc4680b);SHA1(641d7b57c442074136f01fe288175ed6621813c5) )
		ROM_LOAD( "08n_12b.bin", 0x06000, 0x2000, CRC(742fafed);SHA1(345683cb9eff1b987721042c36b4d1e0debddd5d) )
		ROM_LOAD( "08j_09b.bin", 0x08000, 0x2000, CRC(1ee63b5c);SHA1(14dea762446cc3c0d4e407dc1e68c2010999fd58) )
		ROM_LOAD( "08k_10b.bin", 0x0a000, 0x2000, CRC(a9f41ec9);SHA1(c24f9d54593e764a0b4530b1a2550b999916992c) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );/* PROMs */
	    ROM_LOAD( "07b.bin",    0x0000, 0x0020, CRC(68db8300);SHA1(33cd6b5ed92d7b73a708f2e4b12b6e7f6496d0c6) )	/* unknown - timing? */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_starforc = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );    /* 64k for code */
		ROM_LOAD( "starforc.3",   0x0000, 0x4000, CRC(8ba27691);SHA1(2b8b1e634ef5bed5c61a078e64a6dda77f84cdf5) )
		ROM_LOAD( "starforc.2",   0x4000, 0x4000, CRC(0fc4d2d6);SHA1(0743e3928d5cc0e3f1bcdaf4b0cc83aeb7a2f7a8) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );    /* 64k for sound board */
		ROM_LOAD( "starforc.1",   0x0000, 0x2000, CRC(2735bb22);SHA1(1bd0558e05b41aebab3911991969512df904fea5) )
	
		ROM_REGION( 0x03000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "starforc.7",   0x00000, 0x1000, CRC(f4803339);SHA1(a119d68c2dd1c0e191231ce77353b31f30f7aa76) )	/* fg */
		ROM_LOAD( "starforc.8",   0x01000, 0x1000, CRC(96979684);SHA1(bb4f7d3afc8dfaa723dfb5374996cc4bfd76fa3c) )
		ROM_LOAD( "starforc.9",   0x02000, 0x1000, CRC(eead1d5c);SHA1(7c9165ed227c5228122b494a265cbfd6e843ba61) )
	
		ROM_REGION( 0x06000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "starforc.15",  0x00000, 0x2000, CRC(c3bda12f);SHA1(3748ea8e34222a31a365a02ec77430f268b0b397) )	/* bg1 */
		ROM_LOAD( "starforc.14",  0x02000, 0x2000, CRC(9e9384fe);SHA1(3aaa9cc64ef3775325f64733da4f6c328abf6514) )
		ROM_LOAD( "starforc.13",  0x04000, 0x2000, CRC(84603285);SHA1(f4d6dfa3968fbd8ebf1a6451d5ea1821d65d9b49) )
	
		ROM_REGION( 0x06000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "starforc.12",  0x00000, 0x2000, CRC(fdd9e38b);SHA1(3766835d9e9fc7e5dd99521e7303562029b78a65) )	/* bg2 */
		ROM_LOAD( "starforc.11",  0x02000, 0x2000, CRC(668aea14);SHA1(62eb0df48f2f0c5778bb230cc3bf0b8eb3b4e3f8) )
		ROM_LOAD( "starforc.10",  0x04000, 0x2000, CRC(c62a19c1);SHA1(9ce0e29630d3c8cba4db4cff333b250481348968) )
	
		ROM_REGION( 0x03000, REGION_GFX4, ROMREGION_DISPOSE );
		ROM_LOAD( "starforc.18",  0x00000, 0x1000, CRC(6455c3ad);SHA1(b163ccd3dc26ccfa8be1d16d52e17bc660ff84e3) )	/* bg3 */
		ROM_LOAD( "starforc.17",  0x01000, 0x1000, CRC(68c60d0f);SHA1(1152ba0c274ecadb534133a860bbc8a93577dcf2) )
		ROM_LOAD( "starforc.16",  0x02000, 0x1000, CRC(ce20b469);SHA1(60177a669d9c8cbeedd03ca5e2edf3f589c1c815) )
	
		ROM_REGION( 0x0c000, REGION_GFX5, ROMREGION_DISPOSE );
		ROM_LOAD( "starforc.6",   0x00000, 0x4000, CRC(5468a21d);SHA1(4a1196d4cfb99616efdac9b3927609a85c6f1758) )	/* sprites */
		ROM_LOAD( "starforc.5",   0x04000, 0x4000, CRC(f71717f8);SHA1(bf673571f772d8e0eddae89c00f31390c49a25d2) )
		ROM_LOAD( "starforc.4",   0x08000, 0x4000, CRC(dd9d68a4);SHA1(34c60d2b34c7980bf65a5ebadb9c73f89128141f) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );/* PROMs */
	    ROM_LOAD( "07b.bin",    0x0000, 0x0020, CRC(68db8300);SHA1(33cd6b5ed92d7b73a708f2e4b12b6e7f6496d0c6) )	/* unknown - timing? */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_starfore = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 2*0x10000, REGION_CPU1, 0 );    /* 64k for code + 64k for decrypted opcodes */
		ROM_LOAD( "starfore.005", 0x0000, 0x2000, CRC(825f7ebe);SHA1(d63fd516e075bcc28d42189216b95bbf491a4cd1) )
		ROM_LOAD( "starfore.004", 0x2000, 0x2000, CRC(fbcecb65);SHA1(0406ae134915539a171603ecdd1b549f98dd048c) )
		ROM_LOAD( "starfore.003", 0x4000, 0x2000, CRC(9f8013b9);SHA1(5398c97d84b4458ff926e07d6189d60565fbd8f1) )
		ROM_LOAD( "starfore.002", 0x6000, 0x2000, CRC(f8111eba);SHA1(cf3295ffae4e36e87aea4332613f2cafb51522ce) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );    /* 64k for sound board */
		ROM_LOAD( "starfore.000", 0x0000, 0x2000, CRC(a277c268);SHA1(99ed8439119fa4b850ad8aadb7ff3e54d4cd40be) )
	
		ROM_REGION( 0x03000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "starforc.7",   0x00000, 0x1000, CRC(f4803339);SHA1(a119d68c2dd1c0e191231ce77353b31f30f7aa76) )	/* fg */
		ROM_LOAD( "starforc.8",   0x01000, 0x1000, CRC(96979684);SHA1(bb4f7d3afc8dfaa723dfb5374996cc4bfd76fa3c) )
		ROM_LOAD( "starforc.9",   0x02000, 0x1000, CRC(eead1d5c);SHA1(7c9165ed227c5228122b494a265cbfd6e843ba61) )
	
		ROM_REGION( 0x06000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "starforc.15",  0x00000, 0x2000, CRC(c3bda12f);SHA1(3748ea8e34222a31a365a02ec77430f268b0b397) )	/* bg1 */
		ROM_LOAD( "starforc.14",  0x02000, 0x2000, CRC(9e9384fe);SHA1(3aaa9cc64ef3775325f64733da4f6c328abf6514) )
		ROM_LOAD( "starforc.13",  0x04000, 0x2000, CRC(84603285);SHA1(f4d6dfa3968fbd8ebf1a6451d5ea1821d65d9b49) )
	
		ROM_REGION( 0x06000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "starforc.12",  0x00000, 0x2000, CRC(fdd9e38b);SHA1(3766835d9e9fc7e5dd99521e7303562029b78a65) )	/* bg2 */
		ROM_LOAD( "starforc.11",  0x02000, 0x2000, CRC(668aea14);SHA1(62eb0df48f2f0c5778bb230cc3bf0b8eb3b4e3f8) )
		ROM_LOAD( "starforc.10",  0x04000, 0x2000, CRC(c62a19c1);SHA1(9ce0e29630d3c8cba4db4cff333b250481348968) )
	
		ROM_REGION( 0x03000, REGION_GFX4, ROMREGION_DISPOSE );
		ROM_LOAD( "starforc.18",  0x00000, 0x1000, CRC(6455c3ad);SHA1(b163ccd3dc26ccfa8be1d16d52e17bc660ff84e3) )	/* bg3 */
		ROM_LOAD( "starforc.17",  0x01000, 0x1000, CRC(68c60d0f);SHA1(1152ba0c274ecadb534133a860bbc8a93577dcf2) )
		ROM_LOAD( "starforc.16",  0x02000, 0x1000, CRC(ce20b469);SHA1(60177a669d9c8cbeedd03ca5e2edf3f589c1c815) )
	
		ROM_REGION( 0x0c000, REGION_GFX5, ROMREGION_DISPOSE );
		ROM_LOAD( "starforc.6",   0x00000, 0x4000, CRC(5468a21d);SHA1(4a1196d4cfb99616efdac9b3927609a85c6f1758) )	/* sprites */
		ROM_LOAD( "starforc.5",   0x04000, 0x4000, CRC(f71717f8);SHA1(bf673571f772d8e0eddae89c00f31390c49a25d2) )
		ROM_LOAD( "starforc.4",   0x08000, 0x4000, CRC(dd9d68a4);SHA1(34c60d2b34c7980bf65a5ebadb9c73f89128141f) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );/* PROMs */
	    ROM_LOAD( "07b.bin",    0x0000, 0x0020, CRC(68db8300);SHA1(33cd6b5ed92d7b73a708f2e4b12b6e7f6496d0c6) )	/* unknown - timing? */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_megaforc = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );    /* 64k for code */
		ROM_LOAD( "mf3.bin",      0x0000, 0x4000, CRC(d3ea82ec);SHA1(e15fda65ba24517cc04abc55b5d079a33327553c) )
		ROM_LOAD( "mf2.bin",      0x4000, 0x4000, CRC(aa320718);SHA1(cbbf8e4d06a1ecf77d776058d965afdaa7f5b47f) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );    /* 64k for sound board */
		ROM_LOAD( "starforc.1",   0x0000, 0x2000, CRC(2735bb22);SHA1(1bd0558e05b41aebab3911991969512df904fea5) )
	
		ROM_REGION( 0x03000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "mf7.bin",      0x00000, 0x1000, CRC(43ef8d20);SHA1(07ebe3e10fa56b671788a122cdc02e661b624f40) )	/* fg */
		ROM_LOAD( "mf8.bin",      0x01000, 0x1000, CRC(c36fb746);SHA1(01960e068046bcc0e3e9370fdfe73f9fd64491ae) )
		ROM_LOAD( "mf9.bin",      0x02000, 0x1000, CRC(62e7c9ec);SHA1(24dd1de3e268865c36c732714dc257c58cb88d67) )
	
		ROM_REGION( 0x06000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "starforc.15",  0x00000, 0x2000, CRC(c3bda12f);SHA1(3748ea8e34222a31a365a02ec77430f268b0b397) )	/* bg1 */
		ROM_LOAD( "starforc.14",  0x02000, 0x2000, CRC(9e9384fe);SHA1(3aaa9cc64ef3775325f64733da4f6c328abf6514) )
		ROM_LOAD( "starforc.13",  0x04000, 0x2000, CRC(84603285);SHA1(f4d6dfa3968fbd8ebf1a6451d5ea1821d65d9b49) )
	
		ROM_REGION( 0x06000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "starforc.12",  0x00000, 0x2000, CRC(fdd9e38b);SHA1(3766835d9e9fc7e5dd99521e7303562029b78a65) )	/* bg2 */
		ROM_LOAD( "starforc.11",  0x02000, 0x2000, CRC(668aea14);SHA1(62eb0df48f2f0c5778bb230cc3bf0b8eb3b4e3f8) )
		ROM_LOAD( "starforc.10",  0x04000, 0x2000, CRC(c62a19c1);SHA1(9ce0e29630d3c8cba4db4cff333b250481348968) )
	
		ROM_REGION( 0x03000, REGION_GFX4, ROMREGION_DISPOSE );
		ROM_LOAD( "starforc.18",  0x00000, 0x1000, CRC(6455c3ad);SHA1(b163ccd3dc26ccfa8be1d16d52e17bc660ff84e3) )	/* bg3 */
		ROM_LOAD( "starforc.17",  0x01000, 0x1000, CRC(68c60d0f);SHA1(1152ba0c274ecadb534133a860bbc8a93577dcf2) )
		ROM_LOAD( "starforc.16",  0x02000, 0x1000, CRC(ce20b469);SHA1(60177a669d9c8cbeedd03ca5e2edf3f589c1c815) )
	
		ROM_REGION( 0x0c000, REGION_GFX5, ROMREGION_DISPOSE );
		ROM_LOAD( "starforc.6",   0x00000, 0x4000, CRC(5468a21d);SHA1(4a1196d4cfb99616efdac9b3927609a85c6f1758) )	/* sprites */
		ROM_LOAD( "starforc.5",   0x04000, 0x4000, CRC(f71717f8);SHA1(bf673571f772d8e0eddae89c00f31390c49a25d2) )
		ROM_LOAD( "starforc.4",   0x08000, 0x4000, CRC(dd9d68a4);SHA1(34c60d2b34c7980bf65a5ebadb9c73f89128141f) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );/* PROMs */
	    ROM_LOAD( "07b.bin",    0x0000, 0x0020, CRC(68db8300);SHA1(33cd6b5ed92d7b73a708f2e4b12b6e7f6496d0c6) )	/* unknown - timing? */
	ROM_END(); }}; 
	
	static RomLoadPtr rom_baluba = new RomLoadPtr(){ public void handler(){ 
		ROM_REGION( 0x10000, REGION_CPU1, 0 );    /* 64k for code */
		ROM_LOAD( "0",   		  0x0000, 0x4000, CRC(0e2ebe32);SHA1(d5cac260b19dc4e8d2064a7e3de5d52ab0eb95d0) )
		ROM_LOAD( "1",   		  0x4000, 0x4000, CRC(cde97076);SHA1(ef47851b2ed0d820e1564545795b707d00d5c6ce) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 );    /* 64k for sound board */
		ROM_LOAD( "2",   		  0x0000, 0x2000, CRC(441fbc64);SHA1(3853f80043e28e06a3ee399e3cd261b3ee94e0b9) )
	
		ROM_REGION( 0x03000, REGION_GFX1, ROMREGION_DISPOSE );
		ROM_LOAD( "15",   		  0x00000, 0x1000, CRC(3dda0d84);SHA1(473c307c157bb229a31cd82ce4cdeca1ff604019) )	/* fg */
		ROM_LOAD( "16",   		  0x01000, 0x1000, CRC(3ebc79d8);SHA1(a29b4e314446821cd4a2b1a9d3ff16ee3b6a8f7a) )
		ROM_LOAD( "17",   		  0x02000, 0x1000, CRC(c4430deb);SHA1(e4c18ff2e2c82f3bce346267bc86d4160cb11995) )
	
		ROM_REGION( 0x06000, REGION_GFX2, ROMREGION_DISPOSE );
		ROM_LOAD( "9",  		  0x00000, 0x2000, CRC(90f88c43);SHA1(e4ea963d9c31e34f70aa2b710760e0a102567988) )	/* bg1 */
		ROM_LOAD( "10",  		  0x02000, 0x2000, CRC(ab117070);SHA1(d9a8580f3b0919208801b00501579cf81665fc36) )
		ROM_LOAD( "11",  		  0x04000, 0x2000, CRC(e13b44b0);SHA1(70f3d2465a7652405e23809c81d7ec6ec501835b) )
	
		ROM_REGION( 0x06000, REGION_GFX3, ROMREGION_DISPOSE );
		ROM_LOAD( "12", 		  0x00000, 0x2000, CRC(a6541c8d);SHA1(d7a211c58c2067f257f5a9e343ca4bf689edd514) )	/* bg2 */
		ROM_LOAD( "13", 		  0x02000, 0x2000, CRC(afccdd18);SHA1(d238b52a9bb2dfffaf82ca38bc81c0cbd256f79c) )
		ROM_LOAD( "14", 		  0x04000, 0x2000, CRC(69542e65);SHA1(4119a6f784ed57592d45d325123b261c8f118ca7) )
	
		ROM_REGION( 0x03000, REGION_GFX4, ROMREGION_DISPOSE );
		ROM_LOAD( "8",  		  0x00000, 0x1000, CRC(31e97ef9);SHA1(ed25db4bdaf06f66cfb7179d80425dcb2cb41363) )	/* bg3 */
		ROM_LOAD( "7",  		  0x01000, 0x1000, CRC(5915c5e2);SHA1(58301087d91b34747d5cff3c0dca8e9b441ce62d) )
		ROM_LOAD( "6",  		  0x02000, 0x1000, CRC(ad6881da);SHA1(df629bd9192279b8ebd9d655a94949559e1f118d) )
	
		ROM_REGION( 0x0c000, REGION_GFX5, ROMREGION_DISPOSE );
		ROM_LOAD( "5",  		  0x00000, 0x4000, CRC(3b6b6e96);SHA1(c55f4b6a5f7738a082c02d1adadd9e1d68a0d293) )	/* sprites */
		ROM_LOAD( "4",  		  0x04000, 0x4000, CRC(dd954124);SHA1(f37687197d1564331dc27dace23dec462d02202c) )
		ROM_LOAD( "3",  		  0x08000, 0x4000, CRC(7ac24983);SHA1(4ac32d95af3147af5b9b1af1f292bb629c5d4fb9) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 );/* PROMs */
	    ROM_LOAD( "07b.bin",    0x0000, 0x0020, CRC(68db8300);SHA1(33cd6b5ed92d7b73a708f2e4b12b6e7f6496d0c6) )	/* unknown - timing? */
	ROM_END(); }}; 
	
	
	
	public static GameDriver driver_senjyo	   = new GameDriver("1983"	,"senjyo"	,"senjyo.java"	,rom_senjyo,null	,machine_driver_senjyo	,input_ports_senjyo	,init_senjyo	,ROT90	,	"Tehkan", "Senjyo" )
	public static GameDriver driver_starforc	   = new GameDriver("1984"	,"starforc"	,"senjyo.java"	,rom_starforc,null	,machine_driver_senjyo	,input_ports_starforc	,init_starforc	,ROT90	,	"Tehkan", "Star Force" )
	public static GameDriver driver_starfore	   = new GameDriver("1984"	,"starfore"	,"senjyo.java"	,rom_starfore,driver_starforc	,machine_driver_senjyo	,input_ports_starforc	,init_starfore	,ROT90	,	"Tehkan", "Star Force (encrypted)" )
	public static GameDriver driver_megaforc	   = new GameDriver("1985"	,"megaforc"	,"senjyo.java"	,rom_megaforc,driver_starforc	,machine_driver_senjyo	,input_ports_starforc	,init_starforc	,ROT90	,	"Tehkan (Video Ware license)", "Mega Force" )
	public static GameDriver driver_baluba	   = new GameDriver("1986"	,"baluba"	,"senjyo.java"	,rom_baluba,null	,machine_driver_senjyo	,input_ports_baluba	,init_starforc	,ROT90	,	"Able Corp, Ltd.", "Baluba-louk no Densetsu", GAME_IMPERFECT_COLORS )
}
