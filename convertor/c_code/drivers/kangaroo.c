/***************************************************************************

	Sun Electronics Kangaroo hardware

	driver by Ville Laitinen

	Games supported:
		* Kangaroo
		* Funky Fish

	Known bugs:
		* none at this time

****************************************************************************

	Memory map

****************************************************************************

	========================================================================
	CPU #1
	========================================================================
	0000-7FFF   R     xxxxxxxx    Program ROM
	8000-BFFF   R/W   xxxxxxxx    Bitmap RAM
	C000        R     ----xxxx    Coin inputs
	C200        R     ----xxxx    Option switches
	D000-DFFF   R/W   xxxxxxxx    Custom microprocessor RAM
	E000          W   ----xxxx    BSEL Bank select
	E001          W   xxxxxxxx    DMA ROM start address low
	E002          W   xxxxxxxx    DMA ROM start address high
	E003          W   xxxxxxxx    DMA RAM start address low
	E004          W   xxxxxxxx    DMA RAM start address high
	E005          W   xxxxxxxx    Picture size/DMA start low
	E006          W   xxxxxxxx    Picture size/DMA start high
	========================================================================
	C800          W   xxxxxxxx    Sound chip address
	CA00        R/W   xxxxxxxx    Sound chip data
	========================================================================
	Interrupts:
		NMI not connected
		IRQ generated by VBLANK
	========================================================================

****************************************************************************

	In test mode, to test sound press 1 and 2 player start simultaneously.
	Punch + 1 player start moves to the crosshatch pattern.

	To enter test mode in Funky Fish, keep the service coin pressed while
	resetting

	TODO:
	- There is a custom microcontroller on the original Kangaroo board which is
	  not emulated. This MIGHT cause some problems, but we don't know of any.

***************************************************************************/

/*
CPU #0

0000-0fff tvg75
1000-1fff tvg76
2000-2fff tvg77
3000-3fff tvg78
4000-4fff tvg79
5000-5fff tvg80
8000-bfff VIDEO RAM (four banks)
c000-cfff tvg83/84 (banked)
d000-dfff tvg85/86 (banked)
e000-e3ff RAM


memory mapped ports:
read:
e400      DSW 0
ec00      IN 0
ed00      IN 1
ee00      IN 2
efxx      (4 bits wide) security chip in. It seems to work like a clock.

write:
e800-e801 low/high byte start address of data in picture ROM for DMA
e802-e803 low/high byte start address in bitmap RAM (where picture is to be
          written) during DMA
e804-e805 picture size for DMA, and DMA start
e806      vertical scroll of playfield
e807      horizontal scroll of playfield
e808      bank select latch
e809      A & B bitmap control latch (A=playfield B=motion)
          bit 5 FLIP A
          bit 4 FLIP B
          bit 3 EN A
          bit 2 EN B
          bit 1 PRI A
          bit 0 PRI B
e80a      color shading latch
ec00      command to sound CPU
ed00      coin counters
efxx      (4 bits wide) security chip out

---------------------------------------------------------------------------
CPU #1 (sound)

0000 0fff tvg81
4000 43ff RAM
6000      command from main CPU

I/O ports:
7000      AY-3-8910 write
8000      AY-3-8910 control
---------------------------------------------------------------------------

interrupts:
(CPU#0) standard IM 1 interrupt mode (rst #38 every vblank)
(CPU#1) same here

***************************************************************************/

#include "driver.h"
#include "kangaroo.h"
#include "cpu/z80/z80.h"



/*************************************
 *
 *	Machine init
 *
 *************************************/

static MACHINE_INIT( kangaroo )
{
	/* I think there is a bug in the startup checks of the game. At the very */
	/* beginning, during the RAM check, it goes one byte too far, and ends up */
	/* trying to write, and re-read, location dfff. To the best of my knowledge, */
	/* that is a ROM address, so the test fails and the code keeps jumping back */
	/* at 0000. */
	/* However, a NMI causes a successful reset. Maybe the hardware generates a */
	/* NMI short after power on, therefore masking the bug? The NMI is generated */
	/* by the MB8841 custom microcontroller, so this could be a way to disguise */
	/* the copy protection. */
	/* Anyway, what I do here is just immediately generate the NMI, so the game */
	/* properly starts. */
	cpu_set_irq_line(0, IRQ_LINE_NMI, PULSE_LINE);
}



/*************************************
 *
 *	Custom CPU RAM snooping
 *
 *************************************/

static UINT8 kangaroo_clock=0;


/* I have no idea what the security chip is nor whether it really does,
   this just seems to do the trick -V-
*/

static READ_HANDLER( kangaroo_sec_chip_r )
{
/*  kangaroo_clock = (kangaroo_clock << 1) + 1; */
  kangaroo_clock++;
  return (kangaroo_clock & 0x0f);
}

static WRITE_HANDLER( kangaroo_sec_chip_w )
{
/*  kangaroo_clock = val & 0x0f; */
}



/*************************************
 *
 *	Coin control
 *
 *************************************/

static WRITE_HANDLER( kangaroo_coin_counter_w )
{
	coin_counter_w(0, data & 1);
	coin_counter_w(1, data & 2);
}



/*************************************
 *
 *	Main CPU memory handlers
 *
 *************************************/

static MEMORY_READ_START( readmem )
	{ 0x0000, 0x5fff, MRA_ROM },
	{ 0xc000, 0xdfff, MRA_BANK1 },
	{ 0xe000, 0xe3ff, MRA_RAM },
	{ 0xe400, 0xe400, input_port_3_r },
	{ 0xec00, 0xec00, input_port_0_r },
	{ 0xed00, 0xed00, input_port_1_r },
	{ 0xee00, 0xee00, input_port_2_r },
	{ 0xef00, 0xef00, kangaroo_sec_chip_r },
MEMORY_END


static MEMORY_WRITE_START( writemem )
	{ 0x0000, 0x5fff, MWA_ROM },
	{ 0x8000, 0xbfff, kangaroo_videoram_w },
	{ 0xc000, 0xdfff, MWA_ROM },
	{ 0xe000, 0xe3ff, MWA_RAM },
	{ 0xe800, 0xe805, kangaroo_blitter_w, &kangaroo_blitter },
	{ 0xe806, 0xe807, MWA_RAM, &kangaroo_scroll },
	{ 0xe808, 0xe808, kangaroo_bank_select_w, &kangaroo_bank_select },
	{ 0xe809, 0xe809, kangaroo_video_control_w, &kangaroo_video_control },
	{ 0xe80a, 0xe80a, kangaroo_color_mask_w },
	{ 0xec00, 0xec00, soundlatch_w },
	{ 0xed00, 0xed00, kangaroo_coin_counter_w },
	{ 0xef00, 0xefff, kangaroo_sec_chip_w },
MEMORY_END



/*************************************
 *
 *	Sound CPU memory handlers
 *
 *************************************/

static MEMORY_READ_START( sound_readmem )
	{ 0x0000, 0x0fff, MRA_ROM },
	{ 0x4000, 0x43ff, MRA_RAM },
	{ 0x6000, 0x6000, soundlatch_r },
MEMORY_END


static MEMORY_WRITE_START( sound_writemem )
	{ 0x0000, 0x0fff, MWA_ROM },
	{ 0x4000, 0x43ff, MWA_RAM },
MEMORY_END


static PORT_WRITE_START( sound_writeport )
	{ 0x7000, 0x7000, AY8910_write_port_0_w },
	{ 0x8000, 0x8000, AY8910_control_port_0_w },
PORT_END



/*************************************
 *
 *	Port definitions
 *
 *************************************/

INPUT_PORTS_START( fnkyfish )
	PORT_START      /* IN0 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_SERVICE1 )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START1 )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START2 )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_COIN1 )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* IN1 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* DSW0 */
	PORT_DIPNAME( 0x01, 0x00, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x00, "3" )
	PORT_DIPSETTING(    0x01, "5" )
	PORT_DIPNAME( 0x02, 0x02, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(    0x02, DEF_STR( Upright ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Cocktail ) )
	PORT_DIPNAME( 0x04, 0x00, DEF_STR( Flip_Screen ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x04, DEF_STR( On ) )
	PORT_DIPNAME( 0x08, 0x00, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x08, DEF_STR( On ) )
	PORT_DIPNAME( 0x10, 0x00, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x10, DEF_STR( On ) )
	PORT_DIPNAME( 0x20, 0x00, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x20, DEF_STR( On ) )
	PORT_DIPNAME( 0x40, 0x00, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x40, DEF_STR( On ) )
	PORT_DIPNAME( 0x80, 0x00, DEF_STR( Unknown ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x80, DEF_STR( On ) )
INPUT_PORTS_END


INPUT_PORTS_START( kangaroo )
	PORT_START      /* IN0 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_SERVICE1 )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_START1 )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_START2 )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_COIN1 )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_COIN2 )
	PORT_DIPNAME( 0x20, 0x00, "Music" )
	PORT_DIPSETTING(    0x20, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x00, DEF_STR( On ) )
	PORT_DIPNAME( 0x40, 0x00, DEF_STR( Cabinet ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Upright ) )
	PORT_DIPSETTING(    0x40, DEF_STR( Cocktail ) )
	PORT_DIPNAME( 0x80, 0x00, DEF_STR( Flip_Screen ) )
	PORT_DIPSETTING(    0x00, DEF_STR( Off ) )
	PORT_DIPSETTING(    0x80, DEF_STR( On ) )

	PORT_START      /* IN1 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_SERVICE( 0x80, IP_ACTIVE_HIGH )

	PORT_START      /* IN2 */
	PORT_BIT( 0x01, IP_ACTIVE_HIGH, IPT_JOYSTICK_RIGHT | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x02, IP_ACTIVE_HIGH, IPT_JOYSTICK_LEFT  | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x04, IP_ACTIVE_HIGH, IPT_JOYSTICK_UP    | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x08, IP_ACTIVE_HIGH, IPT_JOYSTICK_DOWN  | IPF_8WAY | IPF_COCKTAIL )
	PORT_BIT( 0x10, IP_ACTIVE_HIGH, IPT_BUTTON1 | IPF_COCKTAIL )
	PORT_BIT( 0x20, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x40, IP_ACTIVE_HIGH, IPT_UNKNOWN )
	PORT_BIT( 0x80, IP_ACTIVE_HIGH, IPT_UNKNOWN )

	PORT_START      /* DSW0 */
	PORT_DIPNAME( 0x01, 0x00, DEF_STR( Lives ) )
	PORT_DIPSETTING(    0x00, "3" )
	PORT_DIPSETTING(    0x01, "5" )
	PORT_DIPNAME( 0x02, 0x00, DEF_STR( Difficulty ) )
	PORT_DIPSETTING(    0x00, "Easy" )
	PORT_DIPSETTING(    0x02, "Hard" )
	PORT_DIPNAME( 0x0c, 0x00, DEF_STR( Bonus_Life ) )
	PORT_DIPSETTING(    0x08, "10000 30000" )
	PORT_DIPSETTING(    0x0c, "20000 40000" )
	PORT_DIPSETTING(    0x04, "10000" )
	PORT_DIPSETTING(    0x00, "None" )
	PORT_DIPNAME( 0xf0, 0x00, DEF_STR( Coinage ) )
	PORT_DIPSETTING(    0x10, DEF_STR( 2C_1C ) )
	PORT_DIPSETTING(    0x20, "A 2C/1C B 1C/3C" )
	PORT_DIPSETTING(    0x00, DEF_STR( 1C_1C ) )
	PORT_DIPSETTING(    0x30, "A 1C/1C B 1C/2C" )
	PORT_DIPSETTING(    0x40, "A 1C/1C B 1C/3C" )
	PORT_DIPSETTING(    0x50, "A 1C/1C B 1C/4C" )
	PORT_DIPSETTING(    0x60, "A 1C/1C B 1C/5C" )
	PORT_DIPSETTING(    0x70, "A 1C/1C B 1C/6C" )
	PORT_DIPSETTING(    0x80, DEF_STR( 1C_2C ) )
	PORT_DIPSETTING(    0x90, "A 1C/2C B 1C/4C" )
	PORT_DIPSETTING(    0xa0, "A 1C/2C B 1C/5C" )
	PORT_DIPSETTING(    0xe0, "A 1C/2C B 1C/6C" )
	PORT_DIPSETTING(    0xb0, "A 1C/2C B 1C/10C" )
	PORT_DIPSETTING(    0xc0, "A 1C/2C B 1C/11C" )
	PORT_DIPSETTING(    0xd0, "A 1C/2C B 1C/12C" )
	/* 0xe0 gives A 1/2 B 1/6 */
	PORT_DIPSETTING(    0xf0, DEF_STR( Free_Play ) )
INPUT_PORTS_END



/*************************************
 *
 *	Sound definitions
 *
 *************************************/

static struct AY8910interface ay8910_interface =
{
	1,  /* 1 chip */
	10000000/8,     /* 1.25 MHz */
	{ 50 },
	{ 0 },
	{ 0 },
	{ 0 },
	{ 0 }
};



/*************************************
 *
 *	Machine driver
 *
 *************************************/

static MACHINE_DRIVER_START( kangaroo )

	/* basic machine hardware */
	MDRV_CPU_ADD(Z80, 10000000/4)	/* 2.5 MHz */
	MDRV_CPU_MEMORY(readmem,writemem)
	MDRV_CPU_VBLANK_INT(irq0_line_hold,1)

	MDRV_CPU_ADD(Z80, 10000000/4)	/* 2.5 MHz */
	MDRV_CPU_FLAGS(CPU_16BIT_PORT | CPU_AUDIO_CPU)
	MDRV_CPU_MEMORY(sound_readmem,sound_writemem)
	MDRV_CPU_PORTS(0,sound_writeport)
	MDRV_CPU_VBLANK_INT(irq0_line_hold,1)

	MDRV_FRAMES_PER_SECOND(60)
	MDRV_VBLANK_DURATION(DEFAULT_60HZ_VBLANK_DURATION)

	MDRV_MACHINE_INIT(kangaroo)

	/* video hardware */
	MDRV_VIDEO_ATTRIBUTES(VIDEO_TYPE_RASTER)
	MDRV_SCREEN_SIZE(32*8, 32*8)
	MDRV_VISIBLE_AREA(0, 255, 8, 255-8)
	MDRV_PALETTE_LENGTH(24)

	MDRV_PALETTE_INIT(kangaroo)
	MDRV_VIDEO_START(kangaroo)
	MDRV_VIDEO_UPDATE(kangaroo)

	/* sound hardware */
	MDRV_SOUND_ADD(AY8910, ay8910_interface)
MACHINE_DRIVER_END



/*************************************
 *
 *	ROM definitions
 *
 *************************************/

ROM_START( fnkyfish )
	ROM_REGION( 0x14000, REGION_CPU1, 0 )
	ROM_LOAD( "tvg_64.0",    0x0000,  0x1000, CRC(af728803) SHA1(1cbbf863f0eb4c759d6037ef9d9d0f4586b7b570) )
	ROM_LOAD( "tvg_65.1",    0x1000,  0x1000, CRC(71959e6b) SHA1(7336cbf3eefd081cd657a56fb6a8fbdac1b51c2c) )
	ROM_LOAD( "tvg_66.2",    0x2000,  0x1000, CRC(5ccf68d4) SHA1(c885df8b2b1bcb578ceab6615caf633dac02a5b2) )
	ROM_LOAD( "tvg_67.3",    0x3000,  0x1000, CRC(938ff36f) SHA1(bf660217ff82d5850ab97238ed2e32199d04f8c9) )

	ROM_REGION( 0x10000, REGION_CPU2, 0 )
	ROM_LOAD( "tvg_68.8",    0x0000,  0x1000, CRC(d36bb2be) SHA1(330160161857407fda62f16e7f43b8833744fd34) )

	ROM_REGION( 0x4000, REGION_GFX1, 0 )
	ROM_LOAD( "tvg_69.v0",   0x0000, 0x1000, CRC(cd532d0b) SHA1(7a64f8bab1a0feafd53a4b81ac3b624a7c1bd26a) ) /* graphics ROMs */
	ROM_LOAD( "tvg_71.v2",   0x1000, 0x1000, CRC(a59c9713) SHA1(60dafa3d5a70b7e727b7c4688f8f3125735c31ec) )
	ROM_LOAD( "tvg_70.v1",   0x2000, 0x1000, CRC(fd308ef1) SHA1(d07f964cab875b0e47f3469fa5211684a5725dfe) )
	ROM_LOAD( "tvg_72.v3",   0x3000, 0x1000, CRC(6ae9b584) SHA1(408d26f4cdcd2abf0667fdc9c6eae58c9052981d) )
ROM_END


ROM_START( kangaroo )
	ROM_REGION( 0x14000, REGION_CPU1, 0 )
	ROM_LOAD( "tvg_75.0",    0x0000, 0x1000, CRC(0d18c581) SHA1(0e0f89d644b79e887c53e5294783843ca7e875ba) )
	ROM_LOAD( "tvg_76.1",    0x1000, 0x1000, CRC(5978d37a) SHA1(684c1092de4a0927a03752903c86c3bbe99e868a) )
	ROM_LOAD( "tvg_77.2",    0x2000, 0x1000, CRC(522d1097) SHA1(09fe627a46d32df2e098d9fad7757f9d61bef41f) )
	ROM_LOAD( "tvg_78.3",    0x3000, 0x1000, CRC(063da970) SHA1(582ff21dd46c651f07a4846e0f8a7544a5891988) )
	ROM_LOAD( "tvg_79.4",    0x4000, 0x1000, CRC(9e5cf8ca) SHA1(015387f038c5670f88c9b22453d074bd9b2a129d) )
	ROM_LOAD( "tvg_80.5",    0x5000, 0x1000, CRC(2fc18049) SHA1(31fcac8eb660739a1672346136a1581a5ef20325) )

	ROM_REGION( 0x10000, REGION_CPU2, 0 )
	ROM_LOAD( "tvg_81.8",    0x0000, 0x1000, CRC(fb449bfd) SHA1(f593a0339f47e121736a927587132aeb52704557) )

	ROM_REGION( 0x0800, REGION_CPU3, 0 )  /* 8k for the MB8841 custom microcontroller (currently not emulated) */
	ROM_LOAD( "tvg_82.12",   0x0000, 0x0800, CRC(57766f69) SHA1(94a7a557d8325799523d5e1a88653a9a3fbe34f9) )

	ROM_REGION( 0x4000, REGION_GFX1, 0 )
	ROM_LOAD( "tvg_83.v0",   0x0000, 0x1000, CRC(c0446ca6) SHA1(fca6ba565051337c0198c93b7b8477632e0dd0b6) )
	ROM_LOAD( "tvg_85.v2",   0x1000, 0x1000, CRC(72c52695) SHA1(87f4715fbb7d509bd9cc4e71e2afb0d475bbac13) )
	ROM_LOAD( "tvg_84.v1",   0x2000, 0x1000, CRC(e4cb26c2) SHA1(5016db9d48fdcfb757618659d063b90862eb0e90) )
	ROM_LOAD( "tvg_86.v3",   0x3000, 0x1000, CRC(9e6a599f) SHA1(76b4eddb4efcd8189d8cc5962d8497e82885f212) )
ROM_END


ROM_START( kangaroa )
	ROM_REGION( 0x10000, REGION_CPU1, 0 )
	ROM_LOAD( "tvg_75.0",    0x0000, 0x1000, CRC(0d18c581) SHA1(0e0f89d644b79e887c53e5294783843ca7e875ba) )
	ROM_LOAD( "tvg_76.1",    0x1000, 0x1000, CRC(5978d37a) SHA1(684c1092de4a0927a03752903c86c3bbe99e868a) )
	ROM_LOAD( "tvg_77.2",    0x2000, 0x1000, CRC(522d1097) SHA1(09fe627a46d32df2e098d9fad7757f9d61bef41f) )
	ROM_LOAD( "tvg_78.3",    0x3000, 0x1000, CRC(063da970) SHA1(582ff21dd46c651f07a4846e0f8a7544a5891988) )
	ROM_LOAD( "tvg79.bin",   0x4000, 0x1000, CRC(82a26c7d) SHA1(09087552dbe4d27df79396072c0f9b916f78f89b) )
	ROM_LOAD( "tvg80.bin",   0x5000, 0x1000, CRC(3dead542) SHA1(0b5d329b1ebbacc650d06289b4e080304e728ea7) )

	ROM_REGION( 0x10000, REGION_CPU2, 0 )
	ROM_LOAD( "tvg_81.8",    0x0000, 0x1000, CRC(fb449bfd) SHA1(f593a0339f47e121736a927587132aeb52704557) )

	ROM_REGION( 0x0800, REGION_CPU3, 0 )  /* 8k for the MB8841 custom microcontroller (currently not emulated) */
	ROM_LOAD( "tvg_82.12",   0x0000, 0x0800, CRC(57766f69) SHA1(94a7a557d8325799523d5e1a88653a9a3fbe34f9) )

	ROM_REGION( 0x4000, REGION_GFX1, 0 )
	ROM_LOAD( "tvg_83.v0",   0x0000, 0x1000, CRC(c0446ca6) SHA1(fca6ba565051337c0198c93b7b8477632e0dd0b6) )
	ROM_LOAD( "tvg_85.v2",   0x1000, 0x1000, CRC(72c52695) SHA1(87f4715fbb7d509bd9cc4e71e2afb0d475bbac13) )
	ROM_LOAD( "tvg_84.v1",   0x2000, 0x1000, CRC(e4cb26c2) SHA1(5016db9d48fdcfb757618659d063b90862eb0e90) )
	ROM_LOAD( "tvg_86.v3",   0x3000, 0x1000, CRC(9e6a599f) SHA1(76b4eddb4efcd8189d8cc5962d8497e82885f212) )
ROM_END


ROM_START( kangarob )
	ROM_REGION( 0x14000, REGION_CPU1, 0 )
	ROM_LOAD( "tvg_75.0",    0x0000, 0x1000, CRC(0d18c581) SHA1(0e0f89d644b79e887c53e5294783843ca7e875ba) )
	ROM_LOAD( "tvg_76.1",    0x1000, 0x1000, CRC(5978d37a) SHA1(684c1092de4a0927a03752903c86c3bbe99e868a) )
	ROM_LOAD( "tvg_77.2",    0x2000, 0x1000, CRC(522d1097) SHA1(09fe627a46d32df2e098d9fad7757f9d61bef41f) )
	ROM_LOAD( "tvg_78.3",    0x3000, 0x1000, CRC(063da970) SHA1(582ff21dd46c651f07a4846e0f8a7544a5891988) )
	ROM_LOAD( "tvg_79.4",    0x4000, 0x1000, CRC(9e5cf8ca) SHA1(015387f038c5670f88c9b22453d074bd9b2a129d) )
	ROM_LOAD( "k6",          0x5000, 0x1000, CRC(7644504a) SHA1(7a8a4bd163d2cdf27390ab2ef65fb7fa6fc1a361) )

	ROM_REGION( 0x10000, REGION_CPU2, 0 )
	ROM_LOAD( "tvg_81.8",    0x0000, 0x1000, CRC(fb449bfd) SHA1(f593a0339f47e121736a927587132aeb52704557) )

	ROM_REGION( 0x4000, REGION_GFX1, 0 )
	ROM_LOAD( "tvg_83.v0",   0x0000, 0x1000, CRC(c0446ca6) SHA1(fca6ba565051337c0198c93b7b8477632e0dd0b6) )
	ROM_LOAD( "tvg_85.v2",   0x1000, 0x1000, CRC(72c52695) SHA1(87f4715fbb7d509bd9cc4e71e2afb0d475bbac13) )
	ROM_LOAD( "tvg_84.v1",   0x2000, 0x1000, CRC(e4cb26c2) SHA1(5016db9d48fdcfb757618659d063b90862eb0e90) )
	ROM_LOAD( "tvg_86.v3",   0x3000, 0x1000, CRC(9e6a599f) SHA1(76b4eddb4efcd8189d8cc5962d8497e82885f212) )
ROM_END



/*************************************
 *
 *	Game drivers
 *
 *************************************/

GAME( 1981, fnkyfish, 0,        kangaroo, fnkyfish, 0, ROT90, "Sun Electronics", "Funky Fish" )
GAME( 1982, kangaroo, 0,        kangaroo, kangaroo, 0, ROT90, "Sun Electronics", "Kangaroo" )
GAME( 1982, kangaroa, kangaroo, kangaroo, kangaroo, 0, ROT90, "[Sun Electronics] (Atari license)", "Kangaroo (Atari)" )
GAME( 1982, kangarob, kangaroo, kangaroo, kangaroo, 0, ROT90, "bootleg", "Kangaroo (bootleg)" )
