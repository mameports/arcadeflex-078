/***************************************************************************

 Super Cobra hardware


TODO:
----

- Need correct color PROMs for Super Bond

- Dark Planet background graphics

- Explosion sound in Scramble/Super Cobra repeats

- Armored Car probably has some other effect(s) during an explosion.
  It uses both POUT1 and POUT2.


Notes/Tidbits:
-------------

- Moonwar: 8255 Port C bit 4 was originally designed so when bit4=0, 1P spinner
  is selected, and when bit4=1, 2P spinner gets selected.  But they forgot to
  change the 8255 initialization value and Port C was set to input, setting the
  spinner select bit to HI regardless what was written to it. This bug has been
  corrected in the newer set, but, to maintain hardware compatibility with
  older PCB's, they had to reverse to active status of the select bit.  So in the
  newer set, Bit4=1 selects the 1P spinner and Bit4=0 selects the 2P spinner.

- Armored Car sets Port C as well, but it's input only and the games uses other
  bits for the 2nd player controls.  Maybe the games was meant to use 2 joysticks
  at one time.

- Calipso was apperantly redesigned for two player simultanious play.
  There is code at $298a to flip the screen, but location $8669 has to be
  set to 2. It's set to 1 no matter how many players are playing.
  It's possible that there is a cocktail version of the game.

- Video Hustler and its two bootlegs all have identical code, the only
  differences are the title, copyright removed, different encryptions or
  no encryption, plus hustlerb has a different memory map.

- In Tazmania, when set to Upright mode, player 2 left skips the current
  level

***************************************************************************/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package drivers;

public class scobra
{
	
	
	
	
	static MEMORY_READ_START( type1_readmem )
		{ 0x0000, 0x7fff, MRA_ROM },
		{ 0x8000, 0x8bff, MRA_RAM },
		{ 0x8c00, 0x8fff, galaxian_videoram_r },	/* mirror */
		{ 0x9000, 0x90ff, MRA_RAM },
		{ 0x9800, 0x9803, ppi8255_0_r },
		{ 0xa000, 0xa003, ppi8255_1_r },
		{ 0xb000, 0xb000, watchdog_reset_r },
	MEMORY_END
	
	static MEMORY_WRITE_START( type1_writemem )
		{ 0x0000, 0x7fff, MWA_ROM },
		{ 0x8000, 0x87ff, MWA_RAM },
		{ 0x8800, 0x8bff, galaxian_videoram_w, &galaxian_videoram },
		{ 0x8c00, 0x8fff, galaxian_videoram_w },	/* mirror */
		{ 0x9000, 0x903f, galaxian_attributesram_w, &galaxian_attributesram },
		{ 0x9040, 0x905f, MWA_RAM, &galaxian_spriteram, &galaxian_spriteram_size },
		{ 0x9060, 0x907f, MWA_RAM, &galaxian_bulletsram, &galaxian_bulletsram_size },
		{ 0x9080, 0x90ff, MWA_RAM },
		{ 0x9800, 0x9803, ppi8255_0_w },
		{ 0xa000, 0xa003, ppi8255_1_w },
		{ 0xa801, 0xa801, galaxian_nmi_enable_w },
		{ 0xa802, 0xa802, galaxian_coin_counter_w },
		{ 0xa804, 0xa804, galaxian_stars_enable_w },
		{ 0xa806, 0xa806, galaxian_flip_screen_x_w },
		{ 0xa807, 0xa807, galaxian_flip_screen_y_w },
	MEMORY_END
	
	
	static MEMORY_READ_START( type2_readmem )
		{ 0x0000, 0x7fff, MRA_ROM },
		{ 0x8000, 0x88ff, MRA_RAM },
		{ 0x9000, 0x93ff, MRA_RAM },
		{ 0x9400, 0x97ff, galaxian_videoram_r },	/* mirror */
		{ 0x9800, 0x9800, watchdog_reset_r},
		{ 0xa000, 0xa00f, scobra_type2_ppi8255_0_r },
		{ 0xa800, 0xa80f, scobra_type2_ppi8255_1_r },
	MEMORY_END
	
	static MEMORY_WRITE_START( type2_writemem )
		{ 0x0000, 0x7fff, MWA_ROM },
		{ 0x8000, 0x87ff, MWA_RAM },
		{ 0x8800, 0x883f, galaxian_attributesram_w, &galaxian_attributesram },
		{ 0x8840, 0x885f, MWA_RAM, &galaxian_spriteram, &galaxian_spriteram_size },
		{ 0x8860, 0x887f, MWA_RAM, &galaxian_bulletsram, &galaxian_bulletsram_size },
		{ 0x8880, 0x88ff, MWA_RAM },
		{ 0x9000, 0x93ff, galaxian_videoram_w, &galaxian_videoram },
		{ 0x9400, 0x97ff, galaxian_videoram_w },	/* mirror */
		{ 0xa000, 0xa00f, scobra_type2_ppi8255_0_w },
		{ 0xa800, 0xa80f, scobra_type2_ppi8255_1_w },
		{ 0xb000, 0xb000, galaxian_stars_enable_w },
		{ 0xb004, 0xb004, galaxian_nmi_enable_w },
		{ 0xb006, 0xb006, galaxian_coin_counter_0_w },
		{ 0xb008, 0xb008, galaxian_coin_counter_1_w },
		{ 0xb00c, 0xb00c, galaxian_flip_screen_y_w },
		{ 0xb00e, 0xb00e, galaxian_flip_screen_x_w },
	MEMORY_END
	
	
	static MEMORY_READ_START( hustler_readmem )
		{ 0x0000, 0x7fff, MRA_ROM },
		{ 0x8000, 0x8bff, MRA_RAM },
		{ 0x9000, 0x90ff, MRA_RAM },
		{ 0xb800, 0xb800, watchdog_reset_r },
		{ 0xd000, 0xd01f, hustler_ppi8255_0_r },
		{ 0xe000, 0xe01f, hustler_ppi8255_1_r },
	MEMORY_END
	
	static MEMORY_WRITE_START( hustler_writemem )
		{ 0x0000, 0x7fff, MWA_ROM },
		{ 0x8000, 0x87ff, MWA_RAM },
		{ 0x8800, 0x8bff, galaxian_videoram_w, &galaxian_videoram },
		{ 0x9000, 0x903f, galaxian_attributesram_w, &galaxian_attributesram },
		{ 0x9040, 0x905f, MWA_RAM, &galaxian_spriteram, &galaxian_spriteram_size },
		{ 0x9060, 0x907f, MWA_RAM, &galaxian_bulletsram, &galaxian_bulletsram_size },
		{ 0x9080, 0x90ff, MWA_RAM },
		{ 0xa802, 0xa802, galaxian_flip_screen_x_w },
		{ 0xa804, 0xa804, galaxian_nmi_enable_w },
		{ 0xa806, 0xa806, galaxian_flip_screen_y_w },
		{ 0xa80e, 0xa80e, MWA_NOP },	/* coin counters */
		{ 0xd000, 0xd01f, hustler_ppi8255_0_w },
		{ 0xe000, 0xe01f, hustler_ppi8255_1_w },
	MEMORY_END
	
	
	static MEMORY_READ_START( hustlerb_readmem )
		{ 0x0000, 0x7fff, MRA_ROM },
		{ 0x8000, 0x8bff, MRA_RAM },
		{ 0x9000, 0x90ff, MRA_RAM },
		{ 0xb000, 0xb000, watchdog_reset_r },
		{ 0xc100, 0xc103, ppi8255_0_r },
		{ 0xc200, 0xc203, ppi8255_1_r },
	MEMORY_END
	
	static MEMORY_WRITE_START( hustlerb_writemem )
		{ 0x0000, 0x3fff, MWA_ROM },
		{ 0x8000, 0x87ff, MWA_RAM },
		{ 0x8800, 0x8bff, galaxian_videoram_w, &galaxian_videoram },
		{ 0x9000, 0x903f, galaxian_attributesram_w, &galaxian_attributesram },
		{ 0x9040, 0x905f, MWA_RAM, &galaxian_spriteram, &galaxian_spriteram_size },
		{ 0x9060, 0x907f, MWA_RAM, &galaxian_bulletsram, &galaxian_bulletsram_size },
		{ 0x9080, 0x90ff, MWA_RAM },
		{ 0xa801, 0xa801, galaxian_nmi_enable_w },
		{ 0xa802, 0xa802, MWA_NOP },	/* coin counters */
		{ 0xa806, 0xa806, galaxian_flip_screen_y_w },
		{ 0xa807, 0xa807, galaxian_flip_screen_x_w },
		{ 0xc100, 0xc103, ppi8255_0_w },
		{ 0xc200, 0xc203, ppi8255_1_w },
	MEMORY_END
	
	
	static MEMORY_READ_START( mimonkey_readmem )
		{ 0x0000, 0x3fff, MRA_ROM },
		{ 0x8000, 0x8bff, MRA_RAM },
		{ 0x8c00, 0x8fff, galaxian_videoram_r },	/* mirror */
		{ 0x9000, 0x90ff, MRA_RAM },
		{ 0x9800, 0x9803, ppi8255_0_r },
		{ 0xa000, 0xa003, ppi8255_1_r },
		{ 0xb000, 0xb000, watchdog_reset_r },
		{ 0xc000, 0xffff, MRA_ROM },
	MEMORY_END
	
	static MEMORY_WRITE_START( mimonkey_writemem )
		{ 0x0000, 0x3fff, MWA_ROM },
		{ 0x8000, 0x87ff, MWA_RAM },
		{ 0x8800, 0x8bff, galaxian_videoram_w, &galaxian_videoram },
		{ 0x8c00, 0x8fff, galaxian_videoram_w },	/* mirror */
		{ 0x9000, 0x903f, galaxian_attributesram_w, &galaxian_attributesram },
		{ 0x9040, 0x905f, MWA_RAM, &galaxian_spriteram, &galaxian_spriteram_size },
		{ 0x9060, 0x907f, MWA_RAM, &galaxian_bulletsram, &galaxian_bulletsram_size },
		{ 0x9080, 0x90ff, MWA_RAM },
		{ 0x9800, 0x9803, ppi8255_0_w },
		{ 0xa000, 0xa003, ppi8255_1_w },
		{ 0xa801, 0xa801, galaxian_nmi_enable_w },
		{ 0xa800, 0xa802, galaxian_gfxbank_w },
		{ 0xa806, 0xa806, galaxian_flip_screen_x_w },
		{ 0xa807, 0xa807, galaxian_flip_screen_y_w },
		{ 0xc000, 0xffff, MWA_ROM },
	MEMORY_END
	
	
	static UINT8 *scobra_soundram;
	
	static READ_HANDLER(scobra_soundram_r)
	{
		return scobra_soundram[offset & 0x03ff];
	}
	
	static WRITE_HANDLER(scobra_soundram_w)
	{
		scobra_soundram[offset & 0x03ff] = data;
	}
	
	MEMORY_READ_START( scobra_sound_readmem )
		{ 0x0000, 0x2fff, MRA_ROM },
		{ 0x8000, 0x8fff, scobra_soundram_r },
	MEMORY_END
	
	MEMORY_WRITE_START( scobra_sound_writemem )
		{ 0x0000, 0x2fff, MWA_ROM },
		{ 0x8000, 0x8fff, scobra_soundram_w },
		{ 0x8000, 0x83ff, MWA_NOP, &scobra_soundram },  /* only here to initialize pointer */
		{ 0x9000, 0x9fff, scramble_filter_w },
	MEMORY_END
	
	
	static MEMORY_WRITE_START( hustlerb_sound_writemem )
		{ 0x0000, 0x1fff, MWA_ROM },
		{ 0x6000, 0x6fff, frogger_filter_w },
		{ 0x8000, 0x83ff, MWA_RAM },
		{ 0x8000, 0x83ff, MWA_NOP, &scobra_soundram },  /* only here to initialize pointer */
	MEMORY_END
	
	
	PORT_READ_START( scobra_sound_readport )
		{ 0x20, 0x20, AY8910_read_port_0_r },
		{ 0x80, 0x80, AY8910_read_port_1_r },
	PORT_END
	
	PORT_WRITE_START( scobra_sound_writeport )
		{ 0x10, 0x10, AY8910_control_port_0_w },
		{ 0x20, 0x20, AY8910_write_port_0_w },
		{ 0x40, 0x40, AY8910_control_port_1_w },
		{ 0x80, 0x80, AY8910_write_port_1_w },
	PORT_END
	
	
	static PORT_READ_START( hustlerb_sound_readport )
		{ 0x80, 0x80, AY8910_read_port_0_r },
	PORT_END
	
	static PORT_WRITE_START( hustlerb_sound_writeport )
		{ 0x40, 0x40, AY8910_control_port_0_w },
		{ 0x80, 0x80, AY8910_write_port_0_w },
	PORT_END
	
	
	INPUT_PORTS_START( scobra )
		PORT_START      /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START      /* IN1 */
		PORT_DIPNAME( 0x01, 0x00, "Allow Continue" )
		PORT_DIPSETTING(    0x00, DEF_STR( No ) )
		PORT_DIPSETTING(    0x01, DEF_STR( Yes ) )
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x00, "3" )
		PORT_DIPSETTING(    0x02, "4" )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 )
	
		PORT_START      /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL )
		PORT_DIPNAME( 0x06, 0x02, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x04, DEF_STR( 2C_1C ) )
		PORT_DIPSETTING(    0x06, DEF_STR( 4C_3C ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x00, "1 Coin/99 Credits" )
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( Cabinet ) )
		PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
		PORT_DIPSETTING(    0x08, DEF_STR( Cocktail ) )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN )
	INPUT_PORTS_END
	
	/* identical to scobra apart from the number of lives */
	INPUT_PORTS_START( scobras )
		PORT_START      /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START      /* IN1 */
		PORT_DIPNAME( 0x01, 0x00, "Allow Continue" )
		PORT_DIPSETTING(    0x00, DEF_STR( No ) )
		PORT_DIPSETTING(    0x01, DEF_STR( Yes ) )
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x00, "3" )
		PORT_DIPSETTING(    0x02, "5" )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 )
	
		PORT_START      /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL )
		PORT_DIPNAME( 0x06, 0x02, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x04, DEF_STR( 2C_1C ) )
		PORT_DIPSETTING(    0x06, DEF_STR( 4C_3C ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x00, "1 Coin/99 Credits" )
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( Cabinet ) )
		PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
		PORT_DIPSETTING(    0x08, DEF_STR( Cocktail ) )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN )
	INPUT_PORTS_END
	
	INPUT_PORTS_START( stratgyx )
		PORT_START      /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START      /* IN1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x00, "3" )
		PORT_DIPSETTING(    0x01, "4" )
		PORT_DIPSETTING(    0x02, "5" )
		PORT_BITX( 0,       0x03, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "255", IP_KEY_NONE, IP_JOY_NONE )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 )
	
		PORT_START      /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x06, IP_ACTIVE_LOW, IPT_SPECIAL ) /* lower 2 coinage DIPs */
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( Cabinet ) )
		PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
		PORT_DIPSETTING(    0x08, DEF_STR( Cocktail ) )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP   | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_8WAY )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_COCKTAIL )
	
		PORT_START      /* IN3 */
		PORT_BIT( 0x03, IP_ACTIVE_LOW, IPT_SPECIAL ) /* upper 2 coinage DIPs */
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( Unknown ) )	/* none of these appear to be used */
		PORT_DIPSETTING(    0x04, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x08, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x10, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x20, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x40, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	
		PORT_START      /* IN4 - fake coinage DIPs.  read via IN2 and IN3 */
		PORT_DIPNAME( 0x0f, 0x07, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x0a, "A 2/1  B 1/3" )
	  /*PORT_DIPSETTING(    0x02, "A 2/1  B 1/3" )*/
		PORT_DIPSETTING(    0x09, "A 2/1  B 1/6" )
		PORT_DIPSETTING(    0x03, "A 4/3  B 1/3" )
		PORT_DIPSETTING(    0x0c, "A 1/1  B 2/1" )
		PORT_DIPSETTING(    0x07, "A 1/1  B 1/2" )
		PORT_DIPSETTING(    0x01, "A 1/1  B 1/3" )
		PORT_DIPSETTING(    0x06, "A 1/1  B 1/5" )
	  /*PORT_DIPSETTING(    0x0b, "A 1/1  B 1/5" )*/
		PORT_DIPSETTING(    0x05, "A 1/1  B 1/7" )
		PORT_DIPSETTING(    0x0d, "A 1/2  B 1/1" )
		PORT_DIPSETTING(    0x0e, "A 1/3  B 3/1" )
		PORT_DIPSETTING(    0x0f, "A 1/4  B 4/1" )
		PORT_DIPSETTING(    0x04, "A 1/99  B 2/1" )
	  /*PORT_DIPSETTING(    0x08, "A 1/99  B 2/1" )*/
		PORT_DIPSETTING(    0x00, "A 1/99  B 1/3" )
	INPUT_PORTS_END
	
	INPUT_PORTS_START( armorcar )
		PORT_START	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY | IPF_COCKTAIL )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 )
		PORT_DIPNAME( 0x04, 0x04, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x04, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START	/* IN1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x01, "3" )
		PORT_DIPSETTING(    0x00, "5" )
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( Demo_Sounds ) )
		PORT_DIPSETTING(    0x02, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY | IPF_COCKTAIL )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY | IPF_COCKTAIL )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 )
	
		PORT_START	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY | IPF_COCKTAIL )
		PORT_DIPNAME( 0x06, 0x02, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x00, "Coin A 1/2 Coin B 2/1" )
		PORT_DIPSETTING(    0x04, "Coin A 1/3 Coin B 3/1" )
		PORT_DIPSETTING(    0x06, "Coin A 1/4 Coin B 4/1" )
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( Cabinet ) )
		PORT_DIPSETTING(    0x08, DEF_STR( Upright ) )
		PORT_DIPSETTING(    0x00, DEF_STR( Cocktail ) )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP | IPF_4WAY )
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x20, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN | IPF_4WAY )
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	INPUT_PORTS_END
	
	INPUT_PORTS_START( moonwar )
		PORT_START      /* IN0 */
		PORT_BIT( 0x1f, IP_ACTIVE_LOW, IPT_SPECIAL ) /* the spinner */
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_COCKTAIL )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START      /* IN1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x00, "3" )
		PORT_DIPSETTING(    0x01, "4" )
		PORT_DIPSETTING(    0x02, "5" )
		PORT_DIPSETTING(    0x03, DEF_STR( Free_Play ) )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2  | IPF_COCKTAIL )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 )
	
		PORT_START      /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1  | IPF_COCKTAIL )
		PORT_DIPNAME( 0x06, 0x02, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x00, DEF_STR( 1C_2C ) )
		PORT_DIPSETTING(    0x04, DEF_STR( 1C_3C ) )
		PORT_DIPSETTING(    0x06, DEF_STR( 1C_4C ) )
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( Cabinet ) )
		PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
		PORT_DIPSETTING(    0x08, DEF_STR( Cocktail ) )
		PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNUSED )	/* output bits */
	
		PORT_START      /* IN3/4 - dummy ports for the dial */
		PORT_ANALOG( 0xff, 0x00, IPT_DIAL | IPF_CENTER, 25, 10, 0, 0 )
	
		PORT_START
		PORT_ANALOG( 0xff, 0x00, IPT_DIAL | IPF_CENTER | IPF_COCKTAIL, 25, 10, 0, 0 )
	INPUT_PORTS_END
	
	/* same as above, but coinage is different */
	INPUT_PORTS_START( moonwara )
		PORT_START      /* IN0 */
		PORT_BIT( 0x1f, IP_ACTIVE_LOW, IPT_SPECIAL ) /* the spinner */
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 | IPF_COCKTAIL )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START      /* IN1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x00, "3" )
		PORT_DIPSETTING(    0x01, "4" )
		PORT_DIPSETTING(    0x02, "5" )
		PORT_DIPSETTING(    0x03, DEF_STR( Free_Play ) )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START1 )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_BUTTON2  | IPF_COCKTAIL )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_BUTTON3 )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 )
	
		PORT_START      /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1  | IPF_COCKTAIL )
		PORT_DIPNAME( 0x06, 0x00, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x00, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 1C_2C ) )
		PORT_DIPSETTING(    0x04, DEF_STR( 1C_3C ) )
		PORT_DIPSETTING(    0x06, DEF_STR( 1C_4C ) )
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( Cabinet ) )
		PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
		PORT_DIPSETTING(    0x08, DEF_STR( Cocktail ) )
		PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNUSED )	/* output bits */
	
		PORT_START      /* IN3/4 - dummy ports for the dial */
		PORT_ANALOG( 0xff, 0x00, IPT_DIAL | IPF_CENTER, 25, 10, 0, 0 )
	
		PORT_START		/* doesn't actually work due to bug in game code */
		PORT_ANALOG( 0xff, 0x00, IPT_DIAL | IPF_CENTER | IPF_COCKTAIL, 25, 10, 0, 0 )
	INPUT_PORTS_END
	
	INPUT_PORTS_START( spdcoin )
		PORT_START      /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START1 )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_START2 )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START      /* IN1 */
		PORT_DIPNAME( 0x01, 0x00, "Freeze" )			/* Dip Sw #2 */
		PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x01, DEF_STR( On ) )
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( Free_Play ) )	/* Dip Sw #1 */
		PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x02, DEF_STR( On ) )
		PORT_BIT( 0xfc, IP_ACTIVE_LOW, IPT_UNUSED )
	
		PORT_START      /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNUSED )
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( Unknown ) )		/* Dip Sw #5 - Check code at 0x0569 */
		PORT_DIPSETTING(    0x02, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( Difficulty ) )	/* Dip Sw #4 */
		PORT_DIPSETTING(    0x00, "Easy" )
		PORT_DIPSETTING(    0x04, "Hard" )
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( Lives ) )		/* Dip Sw #3 */
		PORT_DIPSETTING(    0x08, "3" )
		PORT_DIPSETTING(    0x00, "5" )
		PORT_BIT( 0xf0, IP_ACTIVE_LOW, IPT_UNUSED )
	INPUT_PORTS_END
	
	/* cocktail mode is N/A */
	INPUT_PORTS_START( darkplnt )
		PORT_START	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START1 )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON3 )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_START2 )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START	/* IN1 */
		PORT_DIPNAME( 0x01, 0x01, "Bonus Occurrence" )
		PORT_DIPSETTING(    0x01, "Once" )
		PORT_DIPSETTING(    0x00, "Every" )
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x00, "3" )
		PORT_DIPSETTING(    0x02, "5" )
		PORT_ANALOG( 0xfc, 0x00, IPT_DIAL, 25, 10, 0, 0 )	/* scrambled dial */
	
		PORT_START	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_DIPNAME( 0x06, 0x02, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x00, "Coin A 1/2 Coin B 2/1" )
		PORT_DIPSETTING(    0x04, "Coin A 1/3 Coin B 3/1" )
		PORT_DIPSETTING(    0x06, "Coin A 1/4 Coin B 4/1" )
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( Bonus_Life ) )
		PORT_DIPSETTING(    0x00, "100k" )
		PORT_DIPSETTING(    0x08, "200k" )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN )
	INPUT_PORTS_END
	
	INPUT_PORTS_START( tazmania )
		PORT_START	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON2 )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START	/* IN1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x01, "3" )
		PORT_DIPSETTING(    0x00, "5" )
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( Demo_Sounds ) )
		PORT_DIPSETTING(    0x02, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_COCKTAIL )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL )
	
		PORT_START	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 )
		PORT_DIPNAME( 0x06, 0x02, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x00, "Coin A 1/2 Coin B 2/1" )
		PORT_DIPSETTING(    0x04, "Coin A 1/3 Coin B 3/1" )
		PORT_DIPSETTING(    0x06, "Coin A 1/4 Coin B 4/1" )
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( Cabinet ) )
		PORT_DIPSETTING(    0x08, DEF_STR( Upright ) )
		PORT_DIPSETTING(    0x00, DEF_STR( Cocktail ) )
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x10, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x20, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 )
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	INPUT_PORTS_END
	
	/* cocktail mode is N/A */
	INPUT_PORTS_START( calipso )
		PORT_START      /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START      /* IN1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x01, "3" )
		PORT_DIPSETTING(    0x00, "5" )
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( Demo_Sounds ) )
		PORT_DIPSETTING(    0x02, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN )
	
		PORT_START      /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_DIPNAME( 0x06, 0x02, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x00, DEF_STR( 1C_2C ) )
		PORT_DIPSETTING(    0x04, DEF_STR( 1C_3C ) )
		PORT_DIPSETTING(    0x06, DEF_STR( 1C_4C ) )
		PORT_DIPNAME( 0x08, 0x08, "Cabinet (Not Supported)" )
		PORT_DIPSETTING(    0x08, DEF_STR( Upright ) )
		PORT_DIPSETTING(    0x00, DEF_STR( Cocktail ) )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN )
	INPUT_PORTS_END
	
	/* cocktail mode not working due to bug in game code */
	INPUT_PORTS_START( anteater )
		PORT_START	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_4WAY )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_4WAY )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_4WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_4WAY )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START	/* IN1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x01, "3" )
		PORT_DIPSETTING(    0x00, "5" )
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( Demo_Sounds ) )
		PORT_DIPSETTING(    0x02, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN )
	
		PORT_START	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 )
		PORT_DIPNAME( 0x06, 0x02, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x00, "Coin A 1/2 Coin B 2/1" )
		PORT_DIPSETTING(    0x04, "Coin A 1/3 Coin B 3/1" )
		PORT_DIPSETTING(    0x06, "Coin A 1/4 Coin B 4/1" )
		PORT_DIPNAME( 0x08, 0x08, DEF_STR( Cabinet ) )
		PORT_DIPSETTING(    0x08, DEF_STR( Upright ) )
		PORT_DIPSETTING(    0x00, DEF_STR( Cocktail ) )
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x10, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x20, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 )
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	INPUT_PORTS_END
	
	/* cocktail mode is N/A */
	INPUT_PORTS_START( rescue )
		PORT_START	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_DIPNAME( 0x02, 0x02, "Starting Level" )
		PORT_DIPSETTING(    0x02, "1" )
		PORT_DIPSETTING(    0x00, "3" )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP    | IPF_8WAY )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN  | IPF_8WAY )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT  | IPF_8WAY )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START	/* IN1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x01, "3" )
		PORT_DIPSETTING(    0x00, "5" )
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( Demo_Sounds ) )
		PORT_DIPSETTING(    0x02, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_UP    | IPF_8WAY )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_DOWN  | IPF_8WAY )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT  | IPF_8WAY )
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x40, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	
		PORT_START	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 )
		PORT_DIPNAME( 0x06, 0x02, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x00, "Coin A 1/2 Coin B 2/1" )
		PORT_DIPSETTING(    0x04, "Coin A 1/3 Coin B 3/1" )
		PORT_DIPSETTING(    0x06, "Coin A 1/4 Coin B 4/1" )
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( Difficulty ) )
		PORT_DIPSETTING(    0x00, "Easy" )
		PORT_DIPSETTING(    0x08, "Hard" )
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x10, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x20, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 )
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	INPUT_PORTS_END
	
	/* cocktail mode is N/A */
	INPUT_PORTS_START( minefld )
		PORT_START	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_DIPNAME( 0x02, 0x02, "Starting Level" )
		PORT_DIPSETTING(    0x02, "1" )
		PORT_DIPSETTING(    0x00, "3" )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP    | IPF_8WAY )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN  | IPF_8WAY )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT  | IPF_8WAY )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START	/* IN1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x01, "3" )
		PORT_DIPSETTING(    0x00, "5" )
		PORT_DIPNAME( 0x02, 0x00, DEF_STR( Demo_Sounds ) )
		PORT_DIPSETTING(    0x02, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_UP    | IPF_8WAY )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_DOWN  | IPF_8WAY )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT  | IPF_8WAY )
		PORT_DIPNAME( 0x40, 0x40, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x40, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	
		PORT_START	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START1 )
		PORT_DIPNAME( 0x02, 0x02, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x00, "Coin A 1/2 Coin B 2/1" )
		PORT_DIPNAME( 0x0c, 0x0c, DEF_STR( Difficulty ) )
		PORT_DIPSETTING(    0x0c, "Easy" )
		PORT_DIPSETTING(    0x08, "Medium" )
		PORT_DIPSETTING(    0x04, "Hard" )
		PORT_DIPSETTING(    0x00, "Hardest" )
		PORT_DIPNAME( 0x10, 0x10, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x10, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x20, 0x20, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x20, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 )
		PORT_DIPNAME( 0x80, 0x80, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	INPUT_PORTS_END
	
	/* cocktail mode is N/A */
	INPUT_PORTS_START( losttomb )
		PORT_START      /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START2 )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START1 )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_UP    | IPF_8WAY )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_DOWN  | IPF_8WAY )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_RIGHT | IPF_8WAY)
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKLEFT_LEFT  | IPF_8WAY )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START      /* IN1 */
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x01, "3" )
		PORT_DIPSETTING(    0x02, "5" )
		PORT_DIPSETTING(    0x03, DEF_STR( Free_Play ) )
		PORT_BITX( 0,       0x00, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_UP    | IPF_8WAY )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_DOWN  | IPF_8WAY )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_RIGHT | IPF_8WAY)
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICKRIGHT_LEFT  | IPF_8WAY )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	
		PORT_START      /* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x01, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x06, 0x02, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x00, "A 1/2 B 2/1" )
		PORT_DIPSETTING(    0x04, "A 1/3 B 3/1" )
		PORT_DIPSETTING(    0x06, "A 1/4 B 4/1" )
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x08, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x10, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x20, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x40, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	INPUT_PORTS_END
	
	/* cocktail mode is N/A */
	INPUT_PORTS_START( superbon )
		PORT_START	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_START2 )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_START1 )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY)
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START	/* IN1 */
		PORT_DIPNAME( 0x03, 0x01, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x01, "3" )
		PORT_DIPSETTING(    0x02, "5" )
		PORT_DIPSETTING(    0x03, DEF_STR( Free_Play ) )
		PORT_BITX( 0,       0x00, IPT_DIPSWITCH_SETTING | IPF_CHEAT, "Invulnerability", IP_KEY_NONE, IP_JOY_NONE )
		PORT_DIPNAME( 0x04, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x04, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x10, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x20, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_BUTTON2 )
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	
		PORT_START	/* DSW0 */
		PORT_DIPNAME( 0x01, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x01, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x06, 0x02, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x00, "A 1/2 B 2/1" )
		PORT_DIPSETTING(    0x04, "A 1/3 B 3/1" )
		PORT_DIPSETTING(    0x06, "A 1/4 B 4/1" )
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x08, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x10, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x10, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x20, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x20, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x40, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x40, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( Unknown ) )
		PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	INPUT_PORTS_END
	
	INPUT_PORTS_START( hustler )
		PORT_START      /* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_SERVICE1 )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START      /* IN1 */
		PORT_DIPNAME( 0x01, 0x01, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x00, "1" )
		PORT_DIPSETTING(    0x01, "2" )
		PORT_BITX(    0x02, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Infinite Lives", IP_KEY_NONE, IP_JOY_NONE )
		PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x02, DEF_STR( On ) )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_COCKTAIL )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 )
	
		PORT_START      /* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL )
		PORT_DIPNAME( 0x06, 0x00, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x04, DEF_STR( 3C_1C ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 2C_1C ) )
		PORT_DIPSETTING(    0x00, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x06, DEF_STR( 1C_2C ) )
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( Cabinet ) )
		PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
		PORT_DIPSETTING(    0x08, DEF_STR( Cocktail ) )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_UNKNOWN )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_UNKNOWN )
	INPUT_PORTS_END
	
	INPUT_PORTS_START( mimonkey )
		PORT_START	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START	/* IN1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x00, "3" )
		PORT_DIPSETTING(    0x01, "4" )
		PORT_DIPSETTING(    0x02, "5" )
		PORT_DIPSETTING(    0x03, "6" )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 )
	
		PORT_START	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 )
		PORT_DIPNAME( 0x06, 0x00, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x06, DEF_STR( 3C_1C ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 2C_1C ) )
		PORT_DIPSETTING(    0x00, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x04, DEF_STR( 1C_2C ) )
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( Cabinet ) )
		PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
		PORT_DIPSETTING(    0x08, DEF_STR( Cocktail ) )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY )
		PORT_BITX(    0x20, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Infinite Lives", IP_KEY_NONE, IP_JOY_NONE )
		PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x20, DEF_STR( On ) )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY )
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( Unknown ) )   /* used, something to do with the bullets */
		PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	INPUT_PORTS_END
	
	/* Same as 'mimonkey' but different "Lives" Dip Switch */
	INPUT_PORTS_START( mimonsco )
		PORT_START	/* IN0 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x02, IP_ACTIVE_LOW, IPT_BUTTON2 )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_UNUSED )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_COIN2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_COIN1 )
	
		PORT_START	/* IN1 */
		PORT_DIPNAME( 0x03, 0x00, DEF_STR( Lives ) )
		PORT_DIPSETTING(    0x00, "1" )
		PORT_DIPSETTING(    0x01, "2" )
		PORT_DIPSETTING(    0x02, "3" )
		PORT_DIPSETTING(    0x03, "4" )
		PORT_BIT( 0x04, IP_ACTIVE_LOW, IPT_BUTTON2 | IPF_PLAYER2 )
		PORT_BIT( 0x08, IP_ACTIVE_LOW, IPT_BUTTON1 | IPF_PLAYER2 )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x20, IP_ACTIVE_LOW, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_PLAYER2 )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_START2 )
		PORT_BIT( 0x80, IP_ACTIVE_LOW, IPT_START1 )
	
		PORT_START	/* IN2 */
		PORT_BIT( 0x01, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_PLAYER2 )
		PORT_DIPNAME( 0x06, 0x00, DEF_STR( Coinage ) )
		PORT_DIPSETTING(    0x06, DEF_STR( 3C_1C ) )
		PORT_DIPSETTING(    0x02, DEF_STR( 2C_1C ) )
		PORT_DIPSETTING(    0x00, DEF_STR( 1C_1C ) )
		PORT_DIPSETTING(    0x04, DEF_STR( 1C_2C ) )
		PORT_DIPNAME( 0x08, 0x00, DEF_STR( Cabinet ) )
		PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
		PORT_DIPSETTING(    0x08, DEF_STR( Cocktail ) )
		PORT_BIT( 0x10, IP_ACTIVE_LOW, IPT_JOYSTICK_UP    | IPF_8WAY )
		PORT_BITX(    0x20, 0x00, IPT_DIPSWITCH_NAME | IPF_CHEAT, "Infinite Lives", IP_KEY_NONE, IP_JOY_NONE )
		PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x20, DEF_STR( On ) )
		PORT_BIT( 0x40, IP_ACTIVE_LOW, IPT_JOYSTICK_DOWN  | IPF_8WAY )
		PORT_DIPNAME( 0x80, 0x00, DEF_STR( Unknown ) )   /* used, something to do with the bullets */
		PORT_DIPSETTING(    0x80, DEF_STR( Off ) )
		PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	INPUT_PORTS_END
	
	
	struct AY8910interface scobra_ay8910_interface =
	{
		2,	/* 2 chips */
		14318000/8,	/* 1.78975 MHz */
		/* Ant Eater clips if the volume is set higher than this */
		{ MIXERG(16,MIXER_GAIN_2x,MIXER_PAN_CENTER), MIXERG(16,MIXER_GAIN_2x,MIXER_PAN_CENTER) },
		{ 0, soundlatch_r },
		{ 0, scramble_portB_r },
		{ 0, 0 },
		{ 0, 0 }
	};
	
	
	static MACHINE_DRIVER_START( type1 )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian_base)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(type1_readmem,type1_writemem)
	
		MDRV_CPU_ADD(Z80,14318000/8)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 1.78975 MHz */
		MDRV_CPU_MEMORY(scobra_sound_readmem,scobra_sound_writemem)
		MDRV_CPU_PORTS(scobra_sound_readport,scobra_sound_writeport)
	
		MDRV_MACHINE_INIT(scramble)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2+1)	/* 32 for characters, 64 for stars, 2 for bullets, 1 for background */
	
		MDRV_PALETTE_INIT(scramble)
		MDRV_VIDEO_START(scramble)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, scobra_ay8910_interface)
	MACHINE_DRIVER_END
	
	
	/* same as regular type 1, the only difference that it has long bullets */
	static MACHINE_DRIVER_START( armorcar )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(type1)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2)	/* 32 for characters, 64 for stars, 2 for bullets */
	
		MDRV_PALETTE_INIT(galaxian)
		MDRV_VIDEO_START(theend)
	MACHINE_DRIVER_END
	
	
	/* same as regular type 1, the only difference is that the bullets are less yellow */
	static MACHINE_DRIVER_START( moonwar )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(type1)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2)	/* 32 for characters, 64 for stars, 2 for bullets */
	
		MDRV_PALETTE_INIT(moonwar)
	MACHINE_DRIVER_END
	
	
	/* Rescue, Minefield and Strategy X have extra colors, and custom video initialise */
	/* routines to set up the graduated color backgound they use */
	static MACHINE_DRIVER_START( rescue )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(type1)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_NEEDS_6BITS_PER_GUN)	/* needs fine color resolution for the gradient background */
		MDRV_PALETTE_LENGTH(32+64+2+128)	/* 32 for characters, 64 for stars, 2 for bullets, 128 for background */
	
		MDRV_PALETTE_INIT(rescue)
		MDRV_VIDEO_START(rescue)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( minefld )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(type1)
	
		/* video hardware */
		MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER | VIDEO_NEEDS_6BITS_PER_GUN)	/* needs fine color resolution for the gradient background */
		MDRV_PALETTE_LENGTH(32+64+2+256)	/* 32 for characters, 64 for stars, 2 for bullets, 256 for background */
	
		MDRV_PALETTE_INIT(minefld)
		MDRV_VIDEO_START(minefld)
	MACHINE_DRIVER_END
	
	
	/* same as the others, but no sprite flipping, but instead the bits are used
	   as extra sprite code bits, giving 256 sprite images */
	static MACHINE_DRIVER_START( calipso )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(type1)
	
		/* video hardware */
		MDRV_VIDEO_START(calipso)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( mimonkey )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(type1)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(mimonkey_readmem,mimonkey_writemem)
	
		/* video hardware */
		MDRV_VIDEO_START(mimonkey)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( type2 )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(type1)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(type2_readmem,type2_writemem)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( stratgyx )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(type2)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2+8)	/* 32 for characters, 64 for stars, 2 for bullets, 8 for background */
	
		MDRV_PALETTE_INIT(stratgyx)
		MDRV_VIDEO_START(stratgyx)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( darkplnt )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(type2)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+2) /* 32 for characters, 2 for bullets */
	
		MDRV_PALETTE_INIT(darkplnt)
		MDRV_VIDEO_START(darkplnt)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( hustler )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian_base)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(hustler_readmem,hustler_writemem)
	
		MDRV_CPU_ADD(Z80,14318000/8)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 1.78975 MHz */
		MDRV_CPU_MEMORY(frogger_sound_readmem,frogger_sound_writemem)
		MDRV_CPU_PORTS(frogger_sound_readport,frogger_sound_writeport)
	
		MDRV_MACHINE_INIT(scramble)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2)	/* 32 for characters, 64 for stars, 2 for bullets */
	
		MDRV_VIDEO_START(scramble)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, frogger_ay8910_interface)
	MACHINE_DRIVER_END
	
	
	static MACHINE_DRIVER_START( hustlerb )
	
		/* basic machine hardware */
		MDRV_IMPORT_FROM(galaxian_base)
		MDRV_CPU_MODIFY("main")
		MDRV_CPU_MEMORY(hustlerb_readmem,hustlerb_writemem)
	
		MDRV_CPU_ADD(Z80,14318000/8)
		MDRV_CPU_FLAGS(CPU_AUDIO_CPU)	/* 1.78975 MHz */
		MDRV_CPU_MEMORY(scobra_sound_readmem,hustlerb_sound_writemem)
		MDRV_CPU_PORTS(hustlerb_sound_readport,hustlerb_sound_writeport)
	
		MDRV_MACHINE_INIT(scramble)
	
		/* video hardware */
		MDRV_PALETTE_LENGTH(32+64+2)	/* 32 for characters, 64 for stars, 2 for bullets */
	
		MDRV_VIDEO_START(scramble)
	
		/* sound hardware */
		MDRV_SOUND_ADD(AY8910, frogger_ay8910_interface)
	MACHINE_DRIVER_END
	
	
	/***************************************************************************
	
	  Game driver(s)
	
	***************************************************************************/
	
	ROM_START( scobra )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "epr1265.2c",   0x0000, 0x1000, CRC(a0744b3f) SHA1(8949298a04f8ba8a82d5d84a7b012a0e7cff11df) )
		ROM_LOAD( "2e",           0x1000, 0x1000, CRC(8e7245cd) SHA1(281504ff364c3ddbf901c92729b139afd93b9785) )
		ROM_LOAD( "epr1267.2f",   0x2000, 0x1000, CRC(47a4e6fb) SHA1(01775ad11dc23469649539ee8fb8a5800df031c6) )
		ROM_LOAD( "2h",           0x3000, 0x1000, CRC(7244f21c) SHA1(f5fff565ed3f6c5f277a4db53c9f569813fcec1d) )
		ROM_LOAD( "epr1269.2j",   0x4000, 0x1000, CRC(e1f8a801) SHA1(2add8270352d6596052d3ff22c891ceccaa92071) )
		ROM_LOAD( "2l",           0x5000, 0x1000, CRC(d52affde) SHA1(5681771ed51d504bdcc2999fcbf926a30b137828) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "5c",           0x0000, 0x0800, CRC(d4346959) SHA1(5eab4505beb69a5bdd88b23db60e1193371250cf) )
		ROM_LOAD( "5d",           0x0800, 0x0800, CRC(cc025d95) SHA1(2b0784c4d05c466e0b7648f16e14f34393d792c3) )
		ROM_LOAD( "5e",           0x1000, 0x0800, CRC(1628c53f) SHA1(ec79a73e4a2d7373454b227dd7eff255f1cc60cc) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "epr1274.5h",   0x0000, 0x0800, CRC(64d113b4) SHA1(7b439bb74d5ecc792e0ca8964bcca8c6b7a51262) )
		ROM_LOAD( "epr1273.5f",   0x0800, 0x0800, CRC(a96316d3) SHA1(9de0e94932e91dc34aea7c81880bde6a486d103b) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, CRC(9b87f90d) SHA1(d11ac5e4a6057301ea2a9cbb404c2b978eb4c1dc) )
	ROM_END
	
	ROM_START( scobras )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "scobra2c.bin", 0x0000, 0x1000, CRC(e15ade38) SHA1(21cf26d1781d133fa336d275d8a61d3f95d10b77) )
		ROM_LOAD( "scobra2e.bin", 0x1000, 0x1000, CRC(a270e44d) SHA1(8b7307af458b9cd3c45bb72b35e682d6d109ed01) )
		ROM_LOAD( "scobra2f.bin", 0x2000, 0x1000, CRC(bdd70346) SHA1(bda0dc5777233a86a3a0aceb6eded45145057ba8) )
		ROM_LOAD( "scobra2h.bin", 0x3000, 0x1000, CRC(dca5ec31) SHA1(50073d44ccef76a3c36c73a6ed4479127f2c98ee) )
		ROM_LOAD( "scobra2j.bin", 0x4000, 0x1000, CRC(0d8f6b6e) SHA1(0ca0096cd55cdb87d14cb7f4c7c7b853ec1661c7) )
		ROM_LOAD( "scobra2l.bin", 0x5000, 0x1000, CRC(6f80f3a9) SHA1(817d212454c5eb16c5d7471d2ccefc4f8708d57f) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "epr1275.5c",   0x0000, 0x0800, CRC(deeb0dd3) SHA1(b815a586f05361b75078d58f1fddfdb36f9d8fae) )
		ROM_LOAD( "epr1276.5d",   0x0800, 0x0800, CRC(872c1a74) SHA1(20f05bf398ad2690f5ba4e4158ad62aeec226413) )
		ROM_LOAD( "epr1277.5e",   0x1000, 0x0800, CRC(ccd7a110) SHA1(5a247e360530be0f94c90fcc7d0ce628d460449f) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "epr1274.5h",   0x0000, 0x0800, CRC(64d113b4) SHA1(7b439bb74d5ecc792e0ca8964bcca8c6b7a51262) )
		ROM_LOAD( "epr1273.5f",   0x0800, 0x0800, CRC(a96316d3) SHA1(9de0e94932e91dc34aea7c81880bde6a486d103b) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, CRC(9b87f90d) SHA1(d11ac5e4a6057301ea2a9cbb404c2b978eb4c1dc) )
	ROM_END
	
	ROM_START( scobrase )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "epr1265.2c",   0x0000, 0x1000, CRC(a0744b3f) SHA1(8949298a04f8ba8a82d5d84a7b012a0e7cff11df) )
		ROM_LOAD( "epr1266.2e",   0x1000, 0x1000, CRC(65306279) SHA1(f6e10d57c9b88e3fcd7333f76708e190a97b4faa) )
		ROM_LOAD( "epr1267.2f",   0x2000, 0x1000, CRC(47a4e6fb) SHA1(01775ad11dc23469649539ee8fb8a5800df031c6) )
		ROM_LOAD( "epr1268.2h",   0x3000, 0x1000, CRC(53eecaf2) SHA1(08ca34097f63af8ab69b1d836a12a8bd4d42e4a2) )
		ROM_LOAD( "epr1269.2j",   0x4000, 0x1000, CRC(e1f8a801) SHA1(2add8270352d6596052d3ff22c891ceccaa92071) )
		ROM_LOAD( "epr1270.2l",   0x5000, 0x1000, CRC(f7709710) SHA1(dff9ae72ba00a98d4f5acdd6d506e3d7add6b2c6) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "epr1275.5c",   0x0000, 0x0800, CRC(deeb0dd3) SHA1(b815a586f05361b75078d58f1fddfdb36f9d8fae) )
		ROM_LOAD( "epr1276.5d",   0x0800, 0x0800, CRC(872c1a74) SHA1(20f05bf398ad2690f5ba4e4158ad62aeec226413) )
		ROM_LOAD( "epr1277.5e",   0x1000, 0x0800, CRC(ccd7a110) SHA1(5a247e360530be0f94c90fcc7d0ce628d460449f) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "epr1274.5h",   0x0000, 0x0800, CRC(64d113b4) SHA1(7b439bb74d5ecc792e0ca8964bcca8c6b7a51262) )
		ROM_LOAD( "epr1273.5f",   0x0800, 0x0800, CRC(a96316d3) SHA1(9de0e94932e91dc34aea7c81880bde6a486d103b) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "pr1278.6e",    0x0000, 0x0020, CRC(fd35c561) SHA1(590f60beb443dd689c890c37cc100e0b936bf8c9) )
	ROM_END
	
	ROM_START( scobrab )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "vid_2c.bin",   0x0000, 0x0800, CRC(aeddf391) SHA1(87632469e943cfe38a9676de5e8ed839a63c5da2) )
		ROM_LOAD( "vid_2e.bin",   0x0800, 0x0800, CRC(72b57eb7) SHA1(978d0acbfccb7c1edddb073ad9417d4cbd9b7e63) )
		ROM_LOAD( "scobra2e.bin", 0x1000, 0x1000, CRC(a270e44d) SHA1(8b7307af458b9cd3c45bb72b35e682d6d109ed01) )
		ROM_LOAD( "scobra2f.bin", 0x2000, 0x1000, CRC(bdd70346) SHA1(bda0dc5777233a86a3a0aceb6eded45145057ba8) )
		ROM_LOAD( "scobra2h.bin", 0x3000, 0x1000, CRC(dca5ec31) SHA1(50073d44ccef76a3c36c73a6ed4479127f2c98ee) )
		ROM_LOAD( "scobra2j.bin", 0x4000, 0x1000, CRC(0d8f6b6e) SHA1(0ca0096cd55cdb87d14cb7f4c7c7b853ec1661c7) )
		ROM_LOAD( "scobra2l.bin", 0x5000, 0x1000, CRC(6f80f3a9) SHA1(817d212454c5eb16c5d7471d2ccefc4f8708d57f) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "snd_5c.bin",   0x0000, 0x0800, CRC(deeb0dd3) SHA1(b815a586f05361b75078d58f1fddfdb36f9d8fae) )
		ROM_LOAD( "snd_5d.bin",   0x0800, 0x0800, CRC(872c1a74) SHA1(20f05bf398ad2690f5ba4e4158ad62aeec226413) )
		ROM_LOAD( "snd_5e.bin",   0x1000, 0x0800, CRC(ccd7a110) SHA1(5a247e360530be0f94c90fcc7d0ce628d460449f) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "epr1274.5h",   0x0000, 0x0800, CRC(64d113b4) SHA1(7b439bb74d5ecc792e0ca8964bcca8c6b7a51262) )
		ROM_LOAD( "epr1273.5f",   0x0800, 0x0800, CRC(a96316d3) SHA1(9de0e94932e91dc34aea7c81880bde6a486d103b) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, CRC(9b87f90d) SHA1(d11ac5e4a6057301ea2a9cbb404c2b978eb4c1dc) )
	ROM_END
	
	ROM_START( stratgyx )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "2c_1.bin",     0x0000, 0x1000, CRC(eec01237) SHA1(619aaf6379064395a1166575f207d61c90731bb9) )
		ROM_LOAD( "2e_2.bin",     0x1000, 0x1000, CRC(926cb2d5) SHA1(241e7b0f0d7e20a79299696be28963fd01269e86) )
		ROM_LOAD( "2f_3.bin",     0x2000, 0x1000, CRC(849e2504) SHA1(0ec00a5c77e9d81d69f8b847a50f36af2dacc8d0) )
		ROM_LOAD( "2h_4.bin",     0x3000, 0x1000, CRC(8a64069b) SHA1(ccc0e0441360e3ed93f2a0dab70c3a66edad969f) )
		ROM_LOAD( "2j_5.bin",     0x4000, 0x1000, CRC(78b9b898) SHA1(158e11352d68ed9e4277efbcb7e927e9bdc662f8) )
		ROM_LOAD( "2l_6.bin",     0x5000, 0x1000, CRC(20bae414) SHA1(cd3c03d3b6a3abb64cb86cffa733d5494cb226f7) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for sound code */
		ROM_LOAD( "s1.bin",       0x0000, 0x1000, CRC(713a5db8) SHA1(8ec41cb93cfd856dc5aecace6238240a5d114ce1) )
		ROM_LOAD( "s2.bin",       0x1000, 0x1000, CRC(46079411) SHA1(72bfc39979818309ac5a49654a825f9e4bd0236c) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "5f_c2.bin",    0x0000, 0x0800, CRC(7121b679) SHA1(bf76c5b777d1f1468b31524bfe250a1bc4911b09) )
		ROM_LOAD( "5h_c1.bin",    0x0800, 0x0800, CRC(d105ad91) SHA1(fd30d001764cf37ae52bab010db27acdc35fece6) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "strategy.6e",  0x0000, 0x0020, CRC(51a629e1) SHA1(f9826202c91a4a3deb8d815cbaa107b29dce5835) )
	
		ROM_REGION( 0x0020, REGION_USER1, 0 )
		ROM_LOAD( "strategy.10k", 0x0000, 0x0020, CRC(d95c0318) SHA1(83e5355fdca7b4e1fb5a0e6eeaacfbf9561e2c36) )	/* background color map */
	ROM_END
	
	ROM_START( stratgys )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "2c.cpu",       0x0000, 0x1000, CRC(f2aaaf2b) SHA1(1aa5d0696961bcd43a4659db0a53f443cd3d3985) )
		ROM_LOAD( "2e.cpu",       0x1000, 0x1000, CRC(5873fdc8) SHA1(9707c6f140cd7d51f8bd1e53f3c138491aa302d1) )
		ROM_LOAD( "2f.cpu",       0x2000, 0x1000, CRC(532d604f) SHA1(eaf314d98005caacde8d2ea6240043ebe10650a5) )
		ROM_LOAD( "2h.cpu",       0x3000, 0x1000, CRC(82b1d95e) SHA1(dc5e79e159990e76353dea7b54e52fadfc9f59ab) )
		ROM_LOAD( "2j.cpu",       0x4000, 0x1000, CRC(66e84cde) SHA1(c1e6b9fe0052fe1f521c943a69d60f65fa392147) )
		ROM_LOAD( "2l.cpu",       0x5000, 0x1000, CRC(62b032d0) SHA1(592866f801839550e60a515978a2d97dd414c87a) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for sound code */
		ROM_LOAD( "s1.bin",       0x0000, 0x1000, CRC(713a5db8) SHA1(8ec41cb93cfd856dc5aecace6238240a5d114ce1) )
		ROM_LOAD( "s2.bin",       0x1000, 0x1000, CRC(46079411) SHA1(72bfc39979818309ac5a49654a825f9e4bd0236c) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "5f.cpu",       0x0000, 0x0800, CRC(f4aa5ddd) SHA1(2be9c8d8b94c599d4ff12864cbbddd7301be9fb2) )
		ROM_LOAD( "5h.cpu",       0x0800, 0x0800, CRC(548e4635) SHA1(fb8d4d498a98b9d19504f1b6c86dfa0110dcc5a7) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "strategy.6e",  0x0000, 0x0020, CRC(51a629e1) SHA1(f9826202c91a4a3deb8d815cbaa107b29dce5835) )
	
		ROM_REGION( 0x0020, REGION_USER1, 0 )
		ROM_LOAD( "strategy.10k", 0x0000, 0x0020, CRC(d95c0318) SHA1(83e5355fdca7b4e1fb5a0e6eeaacfbf9561e2c36) )
	ROM_END
	
	ROM_START( armorcar )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "cpu.2c",       0x0000, 0x1000, CRC(0d7bfdfb) SHA1(47791d4fc298c25d29584dfcddcd92618e3750c5) )
		ROM_LOAD( "cpu.2e",       0x1000, 0x1000, CRC(76463213) SHA1(86dbbed25325cc8855c1eb39bbb11b0473b7f4b5) )
		ROM_LOAD( "cpu.2f",       0x2000, 0x1000, CRC(2cc6d5f0) SHA1(94abb33760aed206f0f90f035fe2977c1f2e26cf) )
		ROM_LOAD( "cpu.2h",       0x3000, 0x1000, CRC(61278dbb) SHA1(e12cd6c499af75f77e549499093fe6d2e8eddb1d) )
		ROM_LOAD( "cpu.2j",       0x4000, 0x1000, CRC(fb158d8c) SHA1(efa70e92c56678d4a404a96c72cfee317b15648c) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "sound.5c",     0x0000, 0x0800, CRC(54ee7753) SHA1(3ebfa2cadf33acb4d24aa50cfa4713355cc780a3) )
		ROM_LOAD( "sound.5d",     0x0800, 0x0800, CRC(5218fec0) SHA1(c8f84f1e6aafc544e5acf48b245e8b1edb63211e) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "cpu.5f",       0x0000, 0x0800, CRC(8a3da4d1) SHA1(4d2ef48aeb9099fdd145e11e2485e0bf8d87290d) )
		ROM_LOAD( "cpu.5h",       0x0800, 0x0800, CRC(85bdb113) SHA1(f62da0ea0c29feb10d8d1ce8de28fd750a53b40a) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, CRC(9b87f90d) SHA1(d11ac5e4a6057301ea2a9cbb404c2b978eb4c1dc) )
	ROM_END
	
	ROM_START( armorca2 )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "2c",           0x0000, 0x1000, CRC(e393bd2f) SHA1(6a5079d1f5d724e5f643cbc5352fc43d0b652e05) )
		ROM_LOAD( "2e",           0x1000, 0x1000, CRC(b7d443af) SHA1(1ce41e11a7fcfd039fbff03c4382ae29b601ed50) )
		ROM_LOAD( "2g",           0x2000, 0x1000, CRC(e67380a4) SHA1(a9a87e769d1ef223ae26241e9211c97b3d469656) )
		ROM_LOAD( "2h",           0x3000, 0x1000, CRC(72af7b37) SHA1(c9cd0a0a3e34fc7b12822f75eb511f0850703f55) )
		ROM_LOAD( "2j",           0x4000, 0x1000, CRC(e6b0dd7f) SHA1(98292fea03bff028ba924a49f0bfa49377018860) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "sound.5c",     0x0000, 0x0800, CRC(54ee7753) SHA1(3ebfa2cadf33acb4d24aa50cfa4713355cc780a3) )
		ROM_LOAD( "sound.5d",     0x0800, 0x0800, CRC(5218fec0) SHA1(c8f84f1e6aafc544e5acf48b245e8b1edb63211e) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "cpu.5f",       0x0000, 0x0800, CRC(8a3da4d1) SHA1(4d2ef48aeb9099fdd145e11e2485e0bf8d87290d) )
		ROM_LOAD( "cpu.5h",       0x0800, 0x0800, CRC(85bdb113) SHA1(f62da0ea0c29feb10d8d1ce8de28fd750a53b40a) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, CRC(9b87f90d) SHA1(d11ac5e4a6057301ea2a9cbb404c2b978eb4c1dc) )
	ROM_END
	
	ROM_START( moonwar )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "mw2.2c",       0x0000, 0x1000, CRC(7c11b4d9) SHA1(a27bdff6ce728647ec811df843ac235c32c293d6) )
		ROM_LOAD( "mw2.2e",       0x1000, 0x1000, CRC(1b6362be) SHA1(2fbd95869146adcc0c8be1df653251fda8849e8e) )
		ROM_LOAD( "mw2.2f",       0x2000, 0x1000, CRC(4fd8ba4b) SHA1(3da784267a96d05f66b00626a22cb3f06211d202) )
		ROM_LOAD( "mw2.2h",       0x3000, 0x1000, CRC(56879f0d) SHA1(d1e9932863aebc5761e71fca8d24f3c400e1250d) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "mw2.5c",       0x0000, 0x0800, CRC(c26231eb) SHA1(5b19edfaefe1a535059311d067ea53405879d627) )
		ROM_LOAD( "mw2.5d",       0x0800, 0x0800, CRC(bb48a646) SHA1(cf51202d16b03bbed12ff24501be68683f28c992) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "mw2.5f",       0x0000, 0x0800, CRC(c5fa1aa0) SHA1(6c6b5b2ce5de278ff436d3e7252ece5b086cc41d) )
		ROM_LOAD( "mw2.5h",       0x0800, 0x0800, CRC(a6ccc652) SHA1(286b3dc1f3a7da3ac66664e774b441ef075745f1) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "mw2.clr",      0x0000, 0x0020, CRC(99614c6c) SHA1(f068985f3c5e0cd88551a02c32f9baeabfd50241) )
	ROM_END
	
	ROM_START( moonwara )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "2c",           0x0000, 0x1000, CRC(bc20b734) SHA1(c6fe550987d0052979aad43c67aa1b9248049669) )
		ROM_LOAD( "2e",           0x1000, 0x1000, CRC(db6ffec2) SHA1(0fcd55b1e415e2e7041d10778052a235251f85fe) )
		ROM_LOAD( "2f",           0x2000, 0x1000, CRC(378931b8) SHA1(663f1eea9b0e8dc38de818df66c5211dac41c33b) )
		ROM_LOAD( "2h",           0x3000, 0x1000, CRC(031dbc2c) SHA1(5f2ca8b8763398bf161ee0c2c748a12d36cb40ec) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "mw2.5c",       0x0000, 0x0800, CRC(c26231eb) SHA1(5b19edfaefe1a535059311d067ea53405879d627) )
		ROM_LOAD( "mw2.5d",       0x0800, 0x0800, CRC(bb48a646) SHA1(cf51202d16b03bbed12ff24501be68683f28c992) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "mw2.5f",       0x0000, 0x0800, CRC(c5fa1aa0) SHA1(6c6b5b2ce5de278ff436d3e7252ece5b086cc41d) )
		ROM_LOAD( "mw2.5h",       0x0800, 0x0800, CRC(a6ccc652) SHA1(286b3dc1f3a7da3ac66664e774b441ef075745f1) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "moonwara.clr", 0x0000, 0x0020, CRC(f58d4f58) SHA1(12a80d1edf3c80dafa0e1e3622d2a03224b62f14) )	/* olive, instead of white */
	ROM_END
	
	ROM_START( spdcoin )
		ROM_REGION( 0x10000, REGION_CPU1, 0 ) /* 64k for code */
		ROM_LOAD( "spdcoin.2c",   0x0000, 0x1000, CRC(65cf1e49) SHA1(a4570f0d7868fcdd378de3fd9e5346780afcf427) )
		ROM_LOAD( "spdcoin.2e",   0x1000, 0x1000, CRC(1ee59232) SHA1(b58c1de69d33cf80432012b9b6d8b1e3d8b00662) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 ) /* 64k for the audio CPU */
		ROM_LOAD( "spdcoin.5c",   0x0000, 0x0800, CRC(b4cf64b7) SHA1(a95d94be2e374b78b4ba49b6931f0c214ff9d033) )
		ROM_LOAD( "spdcoin.5d",   0x0800, 0x0800, CRC(92304df0) SHA1(01471bf7cbea0090933a253b1b46f80c8f240df5) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "spdcoin.5f",   0x0000, 0x0800, CRC(dd5f1dbc) SHA1(e7c40972a7530cac19ce04de3272244959d337ab) )
		ROM_LOAD( "spdcoin.5h",   0x0800, 0x0800, CRC(ab1fe81b) SHA1(98057932cb5faad60d425b547590ab22bfc67ff6) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "spdcoin.clr",  0x0000, 0x0020, CRC(1a2ccc56) SHA1(58bedaa8b3e21e916295603b38529084b6c0099a) )
	ROM_END
	
	ROM_START( darkplnt )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "drkplt2c.dat", 0x0000, 0x1000, CRC(5a0ca559) SHA1(cf77bda30bb08633d274c22734a8f186520ddf7d) )
		ROM_LOAD( "drkplt2e.dat", 0x1000, 0x1000, CRC(52e2117d) SHA1(774e8ee84316135313c4b66c51d21c1661f345b3) )
		ROM_LOAD( "drkplt2g.dat", 0x2000, 0x1000, CRC(4093219c) SHA1(cc92ec4ec8661993173daedfa521252a6e34073d) )
		ROM_LOAD( "drkplt2j.dat", 0x3000, 0x1000, CRC(b974c78d) SHA1(af4c1b21ca8651bfc343a24a106db4c82f0052e7) )
		ROM_LOAD( "drkplt2k.dat", 0x4000, 0x1000, CRC(71a37385) SHA1(a6a358b2f7daa2eaa5bdf96aa903619376473b20) )
		ROM_LOAD( "drkplt2l.dat", 0x5000, 0x1000, CRC(5ad25154) SHA1(20b1347ed5fca108303132d28ae73fc4d71b4e5a) )
		ROM_LOAD( "drkplt2m.dat", 0x6000, 0x1000, CRC(8d2f0122) SHA1(59280abca22fd3d7af53aebf56a9e37eeb7cb11c) )
		ROM_LOAD( "drkplt2p.dat", 0x7000, 0x1000, CRC(2d66253b) SHA1(445c00a74551732fd02df2d5879688f9984931b5) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "5c.snd",       0x0000, 0x1000, CRC(672b9454) SHA1(2b2ec06d62ff5f19a1c802152c798940631fb1d6) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "drkplt5f.dat", 0x0000, 0x0800, CRC(2af0ee66) SHA1(a4f32c256c393a18afde5e3f3c67d7df1223999a) )
		ROM_LOAD( "drkplt5h.dat", 0x0800, 0x0800, CRC(66ef3225) SHA1(87f36a853c4a8ae40d1e3f0482b6e31c86f834f3) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "6e.cpu",       0x0000, 0x0020, CRC(86b6e124) SHA1(76cc422aeb53a4970f91bd5eb00a3e24a76c09d1) )
	ROM_END
	
	ROM_START( tazmania )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "2c.cpu",       0x0000, 0x1000, CRC(932c5a06) SHA1(f90040a12f579a05cb91eacbe49dae9b2c725592) )
		ROM_LOAD( "2e.cpu",       0x1000, 0x1000, CRC(ef17ce65) SHA1(e1372886a4a2ae58278772f49b4f3be35e0b65d1) )
		ROM_LOAD( "2f.cpu",       0x2000, 0x1000, CRC(43c7c39d) SHA1(3cfe97009e3c9236b118fa1beadc50f41584bd7e) )
		ROM_LOAD( "2h.cpu",       0x3000, 0x1000, CRC(be829694) SHA1(3885c95ae1704e7a472139740b87fc8dd9610e07) )
		ROM_LOAD( "2j.cpu",       0x4000, 0x1000, CRC(6e197271) SHA1(231141a95e4dcb54d8bbee346825702e52824c42) )
		ROM_LOAD( "2k.cpu",       0x5000, 0x1000, CRC(a1eb453b) SHA1(50ddfd1dd8cc8c2cde97e52d4ef90e6d10e27a53) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "rom0.snd",     0x0000, 0x0800, CRC(b8d741f1) SHA1(a1bb8a1e0d6b34111f05c539c8e92fffacf5aa5c) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "5f.cpu",       0x0000, 0x0800, CRC(2c5b612b) SHA1(32e3a41a9a4a8b1285b6a195213ff0d98012360a) )
		ROM_LOAD( "5h.cpu",       0x0800, 0x0800, CRC(3f5ff3ac) SHA1(bc70eef54a45b52c14e35464e5f06b5eec554eb6) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "colr6f.cpu",   0x0000, 0x0020, CRC(fce333c7) SHA1(f63a214dc47c5e7c80db000b0b6a261ca8da6629) )
	ROM_END
	
	ROM_START( tazmani2 )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "2ck.cpu",      0x0000, 0x1000, CRC(bf0492bf) SHA1(e665be8b36eb00388daca1bc3a4e4b93b943e2bd) )
		ROM_LOAD( "2ek.cpu",      0x1000, 0x1000, CRC(6636c4d0) SHA1(a0470950111fd9ef3ec71ee9f4b337276db35887) )
		ROM_LOAD( "2fk.cpu",      0x2000, 0x1000, CRC(ce59a57b) SHA1(1ee9a1535f9e06b08db3133a29cc700d1b487e82) )
		ROM_LOAD( "2hk.cpu",      0x3000, 0x1000, CRC(8bda3380) SHA1(eb31dd180da5cb8f8dcdaa777a27ed5514cf38cf) )
		ROM_LOAD( "2jk.cpu",      0x4000, 0x1000, CRC(a4095e35) SHA1(5ccab4cb9c44870852b22511daeda10d54b60822) )
		ROM_LOAD( "2kk.cpu",      0x5000, 0x1000, CRC(f308ca36) SHA1(af360c4ba88b3a3365a2d81575886fcec25f19bf) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "rom0.snd",     0x0000, 0x0800, CRC(b8d741f1) SHA1(a1bb8a1e0d6b34111f05c539c8e92fffacf5aa5c) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "5f.cpu",       0x0000, 0x0800, CRC(2c5b612b) SHA1(32e3a41a9a4a8b1285b6a195213ff0d98012360a) )
		ROM_LOAD( "5h.cpu",       0x0800, 0x0800, CRC(3f5ff3ac) SHA1(bc70eef54a45b52c14e35464e5f06b5eec554eb6) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "colr6f.cpu",   0x0000, 0x0020, CRC(fce333c7) SHA1(f63a214dc47c5e7c80db000b0b6a261ca8da6629) )
	ROM_END
	
	ROM_START( calipso )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "calipso.2c",   0x0000, 0x1000, CRC(0fcb703c) SHA1(2bb096f114911973afdf3088c860c9566df06f60) )
		ROM_LOAD( "calipso.2e",   0x1000, 0x1000, CRC(c6622f14) SHA1(475164aed703a97275ff285ecaec9d8fd4fe723b) )
		ROM_LOAD( "calipso.2f",   0x2000, 0x1000, CRC(7bacbaba) SHA1(d321d6d09c689123eb1e5d758d95ccecec225252) )
		ROM_LOAD( "calipso.2h",   0x3000, 0x1000, CRC(a3a8111b) SHA1(3d9500c676563ebfc27aebb07716e6a966f00c35) )
		ROM_LOAD( "calipso.2j",   0x4000, 0x1000, CRC(fcbd7b9e) SHA1(5cc1edcc8b9867bb7849c8d97d1096bb6464f562) )
		ROM_LOAD( "calipso.2l",   0x5000, 0x1000, CRC(f7630cab) SHA1(482ee91cccd8a7c5768a1d6a9772d797769fe2dc) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for sound code */
		ROM_LOAD( "calipso.5c",   0x0000, 0x0800, CRC(9cbc65ab) SHA1(b4ce04d18f9536c0ddd2f9c15edda75570e750e5) )
		ROM_LOAD( "calipso.5d",   0x0800, 0x0800, CRC(a225ee3b) SHA1(dba111f89851c69fb6fce16219cb2b0cb3294c15) )
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "calipso.5f",   0x0000, 0x2000, CRC(fd4252e9) SHA1(881b988cdc9b7913f577573f8a15af7a7c7cc67f) )
		ROM_LOAD( "calipso.5h",   0x2000, 0x2000, CRC(1663a73a) SHA1(95b6ed25b656afdfb70fac35efa2e005185e4343) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "calipso.clr",  0x0000, 0x0020, CRC(01165832) SHA1(bfef0459492dbd5febf3030916b6438eb6be71de) )
	ROM_END
	
	ROM_START( anteater )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "ra1-2c",       0x0000, 0x1000, CRC(58bc9393) SHA1(7122782a69ef0d2196ec16833f229b6286802668) )
		ROM_LOAD( "ra1-2e",       0x1000, 0x1000, CRC(574fc6f6) SHA1(a1a213d215fe8502edf22383c3a6fb7c9b279d94) )
		ROM_LOAD( "ra1-2f",       0x2000, 0x1000, CRC(2f7c1fe5) SHA1(4cea7e66a85766a9cf9846bb5bc1ca4e6ee1f4e2) )
		ROM_LOAD( "ra1-2h",       0x3000, 0x1000, CRC(ae8a5da3) SHA1(1893d8293b25431d080b89f5b0874440d14e8d17) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "ra4-5c",       0x0000, 0x0800, CRC(87300b4f) SHA1(b81b685ac1d353ff1cd40b876a7478b87b85e7a9) )
		ROM_LOAD( "ra4-5d",       0x0800, 0x0800, CRC(af4e5ffe) SHA1(62717a233cf9f58267af4a9e1c80479b373ab317) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "ra6-5f",       0x0000, 0x0800, CRC(4c3f8a08) SHA1(3152eef64903be1a82f09764821a3654f316197d) )
		ROM_LOAD( "ra6-5h",       0x0800, 0x0800, CRC(b30c7c9f) SHA1(d4ae040d1fd7e5a5d08c2f6968735c551119c207) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "colr6f.cpu",   0x0000, 0x0020, CRC(fce333c7) SHA1(f63a214dc47c5e7c80db000b0b6a261ca8da6629) )
	ROM_END
	
	ROM_START( rescue )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "rb15acpu.bin", 0x0000, 0x1000, CRC(d7e654ba) SHA1(6bcf7a6d86bc2de9a304f0aa9542fdcb58ca73dd) )
		ROM_LOAD( "rb15bcpu.bin", 0x1000, 0x1000, CRC(a93ea158) SHA1(c5550881d2a44f82eeb56d90487137d959c8c6ae) )
		ROM_LOAD( "rb15ccpu.bin", 0x2000, 0x1000, CRC(058cd3d0) SHA1(3f8962be982952adab23c1884cd3a9be5ddd4108) )
		ROM_LOAD( "rb15dcpu.bin", 0x3000, 0x1000, CRC(d6505742) SHA1(804df84ea507bf41e9376a7ce63a278c3701f0cd) )
		ROM_LOAD( "rb15ecpu.bin", 0x4000, 0x1000, CRC(604df3a4) SHA1(15790fa442538632f232280c096ac788d9bf8117) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "rb15csnd.bin", 0x0000, 0x0800, CRC(8b24bf17) SHA1(cc49fef3c629c12f1a7eb9886fdc2df4b08f4b37) )
		ROM_LOAD( "rb15dsnd.bin", 0x0800, 0x0800, CRC(d96e4fb3) SHA1(8bb023c7c668f93d2333d648fc3cefdbd66f92db) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "rb15fcpu.bin", 0x0000, 0x0800, CRC(4489d20c) SHA1(c7a2afbd2e5645a1a25dec6147d61a38ba12380f) )
		ROM_LOAD( "rb15hcpu.bin", 0x0800, 0x0800, CRC(5512c547) SHA1(e0f1c994daaa8933230cbc4bb88d459a698e0d8e) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "rescue.clr",   0x0000, 0x0020, CRC(40c6bcbd) SHA1(cb0c058eadc37eba4b1a99be095da81a14099d8d) )
	ROM_END
	
	ROM_START( aponow )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "aponow01.rom", 0x0000, 0x1000, CRC(33011579) SHA1(62bd821f6e0968a5ed9cbab26a8bff31e707f07a) )
		ROM_LOAD( "aponow02.rom", 0x1000, 0x1000, CRC(d477573e) SHA1(e5deb62f2763ff81e9ca24df67b0539cca8ba714) )
		ROM_LOAD( "aponow03.rom", 0x2000, 0x1000, CRC(46c41898) SHA1(c501f1d75762cfeda68e178e70cdf7eb423005fe) )
		ROM_LOAD( "rb15dcpu.bin", 0x3000, 0x1000, CRC(d6505742) SHA1(804df84ea507bf41e9376a7ce63a278c3701f0cd) ) // aponow04.rom
		ROM_LOAD( "rb15ecpu.bin", 0x4000, 0x1000, CRC(604df3a4) SHA1(15790fa442538632f232280c096ac788d9bf8117) ) // aponow05.rom
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "rb15csnd.bin", 0x0000, 0x0800, CRC(8b24bf17) SHA1(cc49fef3c629c12f1a7eb9886fdc2df4b08f4b37) )
		ROM_LOAD( "rb15dsnd.bin", 0x0800, 0x0800, CRC(d96e4fb3) SHA1(8bb023c7c668f93d2333d648fc3cefdbd66f92db) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "rb15fcpu.bin", 0x0000, 0x0800, CRC(4489d20c) SHA1(c7a2afbd2e5645a1a25dec6147d61a38ba12380f) )
		ROM_LOAD( "rb15hcpu.bin", 0x0800, 0x0800, CRC(5512c547) SHA1(e0f1c994daaa8933230cbc4bb88d459a698e0d8e) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "rescue.clr",   0x0000, 0x0020, CRC(40c6bcbd) SHA1(cb0c058eadc37eba4b1a99be095da81a14099d8d) )
	ROM_END
	
	ROM_START( minefld )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "ma22c",        0x0000, 0x1000, CRC(1367a035) SHA1(25e415743a4cbe573a00b81155f6292592e32cdd) )
		ROM_LOAD( "ma22e",        0x1000, 0x1000, CRC(68946d21) SHA1(62f4501c6d2e8615b1f176e6cea9a75c912de23e) )
		ROM_LOAD( "ma22f",        0x2000, 0x1000, CRC(7663aee5) SHA1(eacb2283e0555176629a96e9b607f0477927d84c) )
		ROM_LOAD( "ma22h",        0x3000, 0x1000, CRC(9787475d) SHA1(161c6f35cc2ce2a88bcc2e9626ccfe1f5e099f28) )
		ROM_LOAD( "ma22j",        0x4000, 0x1000, CRC(2ceceb54) SHA1(c7385ce876582515f6b9a529e0101032056e14e7) )
		ROM_LOAD( "ma22l",        0x5000, 0x1000, CRC(85138fc9) SHA1(b3f2e483150583d2b4daf6a8a308a7f47a0e4f1b) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "ma15c",        0x0000, 0x0800, CRC(8bef736b) SHA1(c5c7ce9c40e6fe60a4914b6bbd646ba6853c9043) )
		ROM_LOAD( "ma15d",        0x0800, 0x0800, CRC(f67b3f97) SHA1(bb15f95eab6594508b5c02af60ed9fff3abd23ee) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "ma15f",        0x0000, 0x0800, CRC(9f703006) SHA1(95c1368b6a3e0cea6fa9406155d3b8d1eb9477f4) )
		ROM_LOAD( "ma15h",        0x0800, 0x0800, CRC(ed0dccb1) SHA1(f1554aa5a4d3a70936645e784a94305f642670df) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "minefld.clr",  0x0000, 0x0020, CRC(1877368e) SHA1(5850e03debe572f72abd91c756c0f8613018a962) )
	ROM_END
	
	ROM_START( losttomb )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "2c",           0x0000, 0x1000, CRC(d6176d2c) SHA1(bc2bf63ee8d3e376f155d218704ceb5adcdf8e54) )
		ROM_LOAD( "2e",           0x1000, 0x1000, CRC(a5f55f4a) SHA1(3609fc2b15b6856e81738bbd370250735dba694d) )
		ROM_LOAD( "2f",           0x2000, 0x1000, CRC(0169fa3c) SHA1(2c06b1deca6c80d067032bfc2386da6ab0111e5f) )
		ROM_LOAD( "2h-easy",      0x3000, 0x1000, CRC(054481b6) SHA1(b0f5d19af0336883e4d9813e58a75c176a63a987) )
		ROM_LOAD( "2j",           0x4000, 0x1000, CRC(249ee040) SHA1(7297039e95e67220fa3e75fc50635f4df4c46a86) )
		ROM_LOAD( "2l",           0x5000, 0x1000, CRC(c7d2e608) SHA1(8aabecabd1dcd6833fb581e4571d71a680e6563a) )
		ROM_LOAD( "2m",           0x6000, 0x1000, CRC(bc4bc5b1) SHA1(95ffa72e57d1da10ddeda4d9333c9e0a2fb33e82) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "5c",           0x0000, 0x0800, CRC(b899be2a) SHA1(9b343a682531255104db61177a43ad933c3af34e) )
		ROM_LOAD( "5d",           0x0800, 0x0800, CRC(6907af31) SHA1(8496c8db5342129d81381eec196facbca45bca77) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "5f",           0x0000, 0x0800, CRC(61f137e7) SHA1(8bff09bc29fa829e21e6b36f7b3f67f19f6bbb26) )
		ROM_LOAD( "5h",           0x0800, 0x0800, CRC(5581de5f) SHA1(763dacb0d2183c159e7f1f04c7ecb1182da18abf) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "ltprom",       0x0000, 0x0020, CRC(1108b816) SHA1(49fdb08f8f31fefa2f3dca3d3455318cb21847a3) )
	ROM_END
	
	ROM_START( losttmbh )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "2c",           0x0000, 0x1000, CRC(d6176d2c) SHA1(bc2bf63ee8d3e376f155d218704ceb5adcdf8e54) )
		ROM_LOAD( "2e",           0x1000, 0x1000, CRC(a5f55f4a) SHA1(3609fc2b15b6856e81738bbd370250735dba694d) )
		ROM_LOAD( "2f",           0x2000, 0x1000, CRC(0169fa3c) SHA1(2c06b1deca6c80d067032bfc2386da6ab0111e5f) )
		ROM_LOAD( "lthard",       0x3000, 0x1000, CRC(e32cbf0e) SHA1(c4a63e01fad7bd450def5c4412690d4bb8d12691) )
		ROM_LOAD( "2j",           0x4000, 0x1000, CRC(249ee040) SHA1(7297039e95e67220fa3e75fc50635f4df4c46a86) )
		ROM_LOAD( "2l",           0x5000, 0x1000, CRC(c7d2e608) SHA1(8aabecabd1dcd6833fb581e4571d71a680e6563a) )
		ROM_LOAD( "2m",           0x6000, 0x1000, CRC(bc4bc5b1) SHA1(95ffa72e57d1da10ddeda4d9333c9e0a2fb33e82) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "5c",           0x0000, 0x0800, CRC(b899be2a) SHA1(9b343a682531255104db61177a43ad933c3af34e) )
		ROM_LOAD( "5d",           0x0800, 0x0800, CRC(6907af31) SHA1(8496c8db5342129d81381eec196facbca45bca77) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "5f",           0x0000, 0x0800, CRC(61f137e7) SHA1(8bff09bc29fa829e21e6b36f7b3f67f19f6bbb26) )
		ROM_LOAD( "5h",           0x0800, 0x0800, CRC(5581de5f) SHA1(763dacb0d2183c159e7f1f04c7ecb1182da18abf) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "ltprom",       0x0000, 0x0020, CRC(1108b816) SHA1(49fdb08f8f31fefa2f3dca3d3455318cb21847a3) )
	ROM_END
	
	ROM_START( superbon )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "2d.cpu",       0x0000, 0x1000, CRC(60c0ba18) SHA1(6ad09e01dd3c86c8d9c465916227c9b00f38e025) )
		ROM_LOAD( "2e.cpu",       0x1000, 0x1000, CRC(ddcf44bf) SHA1(b862622f4aa8af6da568b4f82ef043359ece530f) )
		ROM_LOAD( "2f.cpu",       0x2000, 0x1000, CRC(bb66c2d5) SHA1(cbb7f4279ae48460790cb8abf976b978ae6a1a25) )
		ROM_LOAD( "2h.cpu",       0x3000, 0x1000, CRC(74f4f04d) SHA1(d51c5d2c21453ee0dab60253c3124b6112d1f859) )
		ROM_LOAD( "2j.cpu",       0x4000, 0x1000, CRC(78effb08) SHA1(64f211b34c2f37c25a36200b393f145b39ae67b5) )
		ROM_LOAD( "2l.cpu",       0x5000, 0x1000, CRC(e9dcecbd) SHA1(ec61cec2b66c041872a2ca29cf724a89c73fc9a3) )
		ROM_LOAD( "2m.cpu",       0x6000, 0x1000, CRC(3ed0337e) SHA1(975b93aee851867e335614419aa6db16fbf8063f) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "5c",  	      0x0000, 0x0800, CRC(b899be2a) SHA1(9b343a682531255104db61177a43ad933c3af34e) )
		ROM_LOAD( "5d.snd",       0x0800, 0x0800, CRC(80640a04) SHA1(83f2bafcfa5737441194d3058a76b2582317cfcb) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "5f.cpu",       0x0000, 0x0800, CRC(5b9d4686) SHA1(c7814aefaccab9c8a3a0b015447d366cd2e43c3a) )
		ROM_LOAD( "5h.cpu",       0x0800, 0x0800, CRC(58c29927) SHA1(b88515d9c3108d2ad59f30fed5d74877b1636280) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "superbon.clr", 0x0000, 0x0020, NO_DUMP )
	ROM_END
	
	ROM_START( hustler )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "hustler.1",    0x0000, 0x1000, CRC(94479a3e) SHA1(ea3a1a3f6bee3d35a6a0fb0ba689a25f6b919e5d) )
		ROM_LOAD( "hustler.2",    0x1000, 0x1000, CRC(3cc67bcc) SHA1(bc36eaab68f48c837d0d3d3bc1e7de961fa24a21) )
		ROM_LOAD( "hustler.3",    0x2000, 0x1000, CRC(9422226a) SHA1(445daa94f7236f0776f4692de6cb213f67684f70) )
		/* 3000-3fff space for diagnostics ROM */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "hustler.6",    0x0000, 0x0800, CRC(7a946544) SHA1(7ee2ad3fdf996f08534fb87fc02b619c168f420c) )
		ROM_LOAD( "hustler.7",    0x0800, 0x0800, CRC(3db57351) SHA1(e5075a7130a80d2bf24f0556c2589dff0625ee60) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "hustler.5f",   0x0000, 0x0800, CRC(0bdfad0e) SHA1(8e6f1737604f3801c03fa2e9a5e6a2778b54bae8) )
		ROM_LOAD( "hustler.5h",   0x0800, 0x0800, CRC(8e062177) SHA1(7e52a1669804b6c2f694cfc64b04abc8246bb0c2) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "hustler.clr",  0x0000, 0x0020, CRC(aa1f7f5e) SHA1(311dd17aa11490a1173c76223e4ccccf8ea29850) )
	ROM_END
	
	ROM_START( billiard )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "a",            0x0000, 0x1000, CRC(b7eb50c0) SHA1(213d177d2b2af648a18d196b83e96d804947fd40) )
		ROM_LOAD( "b",            0x1000, 0x1000, CRC(988fe1c5) SHA1(0aaa13006e1832d7c25984b38c2e52fb6a5fdbbc) )
		ROM_LOAD( "c",            0x2000, 0x1000, CRC(7b8de793) SHA1(f54aa0bd558b156c2511a36518963df0e493dbb4) )
		/* 3000-3fff space for diagnostics ROM */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "hustler.6",    0x0000, 0x0800, CRC(7a946544) SHA1(7ee2ad3fdf996f08534fb87fc02b619c168f420c) )
		ROM_LOAD( "hustler.7",    0x0800, 0x0800, CRC(3db57351) SHA1(e5075a7130a80d2bf24f0556c2589dff0625ee60) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "hustler.5f",   0x0000, 0x0800, CRC(0bdfad0e) SHA1(8e6f1737604f3801c03fa2e9a5e6a2778b54bae8) )
		ROM_LOAD( "hustler.5h",   0x0800, 0x0800, CRC(8e062177) SHA1(7e52a1669804b6c2f694cfc64b04abc8246bb0c2) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "hustler.clr",  0x0000, 0x0020, CRC(aa1f7f5e) SHA1(311dd17aa11490a1173c76223e4ccccf8ea29850) )
	ROM_END
	
	/* this is identical to billiard, but with a different memory map */
	ROM_START( hustlerb )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "hustler.2c",   0x0000, 0x1000, CRC(3a1ac6a9) SHA1(f9a6137bab78ce64ec119f199b2bd5ed37e61099) )
		ROM_LOAD( "hustler.2f",   0x1000, 0x1000, CRC(dc6752ec) SHA1(b103021079646286156e4141fe34dd92ccfd34bd) )
		ROM_LOAD( "hustler.2j",   0x2000, 0x1000, CRC(27c1e0f8) SHA1(9713e84b3aa4ed4829de2f62059229564d2b9f72) )
		/* 3000-3fff space for diagnostics ROM */
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "hustler.11d",  0x0000, 0x0800, CRC(b559bfde) SHA1(f7733fbc5cabb441ba039b9d7202aaf0cebb9a85) )
		ROM_LOAD( "hustler.10d",  0x0800, 0x0800, CRC(6ef96cfb) SHA1(eba0bdc8bc1652ff2f62594371ded711dbfcce86) )
	
		ROM_REGION( 0x1000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "hustler.5f",   0x0000, 0x0800, CRC(0bdfad0e) SHA1(8e6f1737604f3801c03fa2e9a5e6a2778b54bae8) )
		ROM_LOAD( "hustler.5h",   0x0800, 0x0800, CRC(8e062177) SHA1(7e52a1669804b6c2f694cfc64b04abc8246bb0c2) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "hustler.clr",  0x0000, 0x0020, CRC(aa1f7f5e) SHA1(311dd17aa11490a1173c76223e4ccccf8ea29850) )
	ROM_END
	
	ROM_START( mimonkey )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "mm1.2e",		  0x0000, 0x1000, CRC(9019f1b1) SHA1(0c45f64e39b9a182f6162ab520ced6ef0686466c) )
		ROM_LOAD( "mm2.2e",		  0x1000, 0x1000, CRC(043e97d6) SHA1(924c0165dfcf01182696b0d259718ac625573d9a) )
		ROM_LOAD( "mm3.2f",		  0x2000, 0x1000, CRC(1052726a) SHA1(2fdd3064f02babd2d496a38c7aee094cb3666f24) )
		ROM_LOAD( "mm4.2h",		  0x3000, 0x1000, CRC(7b3f35ff) SHA1(b52c46c3f166346d3b25cd2ab09781afc703de08) )
		ROM_LOAD( "mm5.2j",		  0xc000, 0x1000, CRC(b4e5c32d) SHA1(18e53519e8f4e813109cfaf45f2f66444e6fa1a2) )
		ROM_LOAD( "mm6.2l",		  0xd000, 0x1000, CRC(409036c4) SHA1(a9640da91156504bfc8fedcda30f81169b28a0c9) )
		ROM_LOAD( "mm7.2m",		  0xe000, 0x1000, CRC(119c08fa) SHA1(6e19ab874b735fe7339bcf651111664263ea4ef9) )
		ROM_LOAD( "mm8.2p",		  0xf000, 0x1000, CRC(f7989f04) SHA1(d6e301414a807f5e9feed92ce53ab73d6bd46c45) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "mm13.11d",	  0x0000, 0x1000, CRC(2d14c527) SHA1(062414ce0415b6c471149319ecae22f465df3a4f) )
		ROM_LOAD( "mm14.10d",	  0x1000, 0x1000, CRC(35ed0f96) SHA1(5aaacae5c2acf97540b72491f71ea823f5eeae1a) )
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "mm12.5h",      0x0000, 0x1000, CRC(f73a8412) SHA1(9baf4336cceb9b039372b0a1c733910aeab5ec6d) )
		ROM_LOAD( "mm10.5h",      0x1000, 0x1000, CRC(3828c9db) SHA1(eaf9e81c803ad2be6c2db3104f07f80788378286) )
		ROM_LOAD( "mm11.5f",      0x2000, 0x1000, CRC(9e0e9289) SHA1(79d412dbceb364bc798feda658b15792feb63338) )
		ROM_LOAD( "mm9.5f",       0x3000, 0x1000, CRC(92085b0c) SHA1(a791703fa9f17e42450c871d902430fc3c6b10ef) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, CRC(4e3caeab) SHA1(a25083c3e36d28afdefe4af6e6d4f3155e303625) )
	ROM_END
	
	ROM_START( mimonsco )
		ROM_REGION( 0x10000, REGION_CPU1, 0 )	/* 64k for code */
		ROM_LOAD( "fra_1a",       0x0000, 0x1000, CRC(8e7a7379) SHA1(06b945a5d237384bfd1b4c9a7449f5a1701a352c) )
		ROM_LOAD( "fra_1b",       0x1000, 0x1000, CRC(ab08cbfe) SHA1(edccefefc0ed476d94acccf7f92115c5d6945679) )
		ROM_LOAD( "fra_2a",       0x2000, 0x1000, CRC(2d4da24d) SHA1(d922713084c9981169f35b41c71c8afa3d7f947d) )
		ROM_LOAD( "fra_2b",       0x3000, 0x1000, CRC(8d88fc7c) SHA1(1ba2d6d448a2c993f398f4457efb1e3535de9ea2) )
		ROM_LOAD( "fra_3a",       0xc000, 0x1000, CRC(b4e5c32d) SHA1(18e53519e8f4e813109cfaf45f2f66444e6fa1a2) )
		ROM_LOAD( "fra_3b",       0xd000, 0x1000, CRC(409036c4) SHA1(a9640da91156504bfc8fedcda30f81169b28a0c9) )
		ROM_LOAD( "fra_4a",       0xe000, 0x1000, CRC(119c08fa) SHA1(6e19ab874b735fe7339bcf651111664263ea4ef9) )
		ROM_LOAD( "fra_4b",       0xf000, 0x1000, CRC(d700fd03) SHA1(3e804a42ecc166d8723f0b0a4906212addbbad7b) )
	
		ROM_REGION( 0x10000, REGION_CPU2, 0 )	/* 64k for the audio CPU */
		ROM_LOAD( "mmsound1",	  0x0000, 0x1000, CRC(2d14c527) SHA1(062414ce0415b6c471149319ecae22f465df3a4f) )
		ROM_LOAD( "mmsnd2a",	  0x1000, 0x1000, CRC(35ed0f96) SHA1(5aaacae5c2acf97540b72491f71ea823f5eeae1a) )
	
		ROM_REGION( 0x4000, REGION_GFX1, ROMREGION_DISPOSE )
		ROM_LOAD( "mmgfx1",		  0x0000, 0x2000, CRC(4af47337) SHA1(225f7bcfbb61e3a163ecaed675d4c81b3609562f) )
		ROM_LOAD( "mmgfx2",		  0x2000, 0x2000, CRC(def47da8) SHA1(8e62e5dc5c810efaa204d0fcb3d02bc84f61ba35) )
	
		ROM_REGION( 0x0020, REGION_PROMS, 0 )
		ROM_LOAD( "82s123.6e",    0x0000, 0x0020, CRC(4e3caeab) SHA1(a25083c3e36d28afdefe4af6e6d4f3155e303625) )
	ROM_END
	
	
	GAME( 1981, scobra,   0,        type1,    scobra,   scobra,       ROT90,  "Konami", "Super Cobra" )
	GAME( 1981, scobras,  scobra,   type1,    scobras,  scobra,       ROT90,  "[Konami] (Stern license)", "Super Cobra (Stern)" )
	GAME( 1981, scobrase, scobra,   type1,    scobras,  scobra,       ROT90,  "[Konami] (Sega license)", "Super Cobra (Sega)" )
	GAME( 1981, scobrab,  scobra,   type1,    scobras,  scobra,       ROT90,  "bootleg", "Super Cobra (bootleg)" )
	GAME( 1981, stratgyx, 0,        stratgyx, stratgyx, stratgyx,     ROT0,   "Konami", "Strategy X" )
	GAME( 1981, stratgys, stratgyx, stratgyx, stratgyx, stratgyx,     ROT0,   "[Konami] (Stern license)", "Strategy X (Stern)" )
	GAME( 1981, armorcar, 0,        armorcar, armorcar, scramble_ppi, ROT90,  "Stern", "Armored Car (set 1)" )
	GAME( 1981, armorca2, armorcar, armorcar, armorcar, scramble_ppi, ROT90,  "Stern", "Armored Car (set 2)" )
	GAME( 1981, moonwar,  0,        moonwar,  moonwar,  moonwar,      ROT90,  "Stern", "Moonwar" )
	GAME( 1981, moonwara, moonwar,  moonwar,  moonwara, moonwar,      ROT90,  "Stern", "Moonwar (older)" )
	GAME( 1984, spdcoin,  0,        type1,    spdcoin,  scramble_ppi, ROT90,  "Stern", "Speed Coin (prototype)" )
	GAME( 1982, darkplnt, 0,        darkplnt, darkplnt, darkplnt,     ROT180, "Stern", "Dark Planet" )
	GAME( 1982, tazmania, 0,        type1,    tazmania, scobra,       ROT90,  "Stern", "Tazz-Mania (set 1)" )
	GAME( 1982, tazmani2, tazmania, type2,    tazmania, tazmani2,     ROT90,  "Stern", "Tazz-Mania (set 2)" )
	GAME( 1982, calipso,  0,        calipso,  calipso,  scobra,       ROT90,  "[Stern] (Tago license)", "Calipso" )
	GAME( 1982, anteater, 0,        type1,    anteater, anteater,     ROT90,  "[Stern] (Tago license)", "Anteater" )
	GAME( 1982, rescue,   0,        rescue,   rescue,   rescue,       ROT90,  "Stern", "Rescue" )
	GAME( 1982, aponow,   rescue,   rescue,   rescue,   rescue,       ROT90,  "bootleg", "Apocaljpse Now" )
	GAME( 1983, minefld,  0,        minefld,  minefld,  minefld,      ROT90,  "Stern", "Minefield" )
	GAME( 1982, losttomb, 0,        type1,    losttomb, losttomb,     ROT90,  "Stern", "Lost Tomb (easy)" )
	GAME( 1982, losttmbh, losttomb, type1,    losttomb, losttomb,     ROT90,  "Stern", "Lost Tomb (hard)" )
	GAMEX(198?, superbon, 0,        type1,    superbon, superbon,     ROT90,  "bootleg", "Super Bond", GAME_WRONG_COLORS )
	GAME( 1981, hustler,  0,        hustler,  hustler,  hustler,      ROT90,  "Konami", "Video Hustler" )
	GAME( 1981, billiard, hustler,  hustler,  hustler,  billiard,     ROT90,  "bootleg", "The Billiards" )
	GAME( 1981, hustlerb, hustler,  hustlerb, hustler,  scramble_ppi, ROT90,  "bootleg", "Video Hustler (bootleg)" )
	GAME( 198?, mimonkey, 0,		mimonkey, mimonkey,	mimonkey,	  ROT90,  "Universal Video Games", "Mighty Monkey" )
	GAME( 198?, mimonsco, mimonkey, mimonkey, mimonsco, mimonsco,     ROT90,  "bootleg", "Mighty Monkey (bootleg on Super Cobra hardware)" )
}
