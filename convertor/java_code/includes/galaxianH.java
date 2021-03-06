/***************************************************************************

  Galaxian hardware family

  This include file is used by the following drivers:
    - galaxian.c
    - scramble.c
    - scobra.c
    - frogger.c
    - amidar.c

***************************************************************************/

/* defined in drivers/galaxian.c */
extern struct GfxDecodeInfo galaxian_gfxdecodeinfo[];
MACHINE_DRIVER_EXTERN(galaxian_base);


/* defined in drivers/scobra.c */
extern struct AY8910interface scobra_ay8910_interface;
extern const struct Memory_ReadAddress scobra_sound_readmem[];
extern const struct Memory_WriteAddress scobra_sound_writemem[];
extern const struct IO_ReadPort scobra_sound_readport[];
extern const struct IO_WritePort scobra_sound_writeport[];


/* defined in drivers/frogger.c */
extern struct AY8910interface frogger_ay8910_interface;
extern const struct Memory_ReadAddress frogger_sound_readmem[];
extern const struct Memory_WriteAddress frogger_sound_writemem[];
extern const struct IO_ReadPort frogger_sound_readport[];
extern const struct IO_WritePort frogger_sound_writeport[];


/* defined in vidhrdw/galaxian.c */
extern data8_t *galaxian_videoram;
extern data8_t *galaxian_spriteram;
extern data8_t *galaxian_spriteram2;
extern data8_t *galaxian_attributesram;
extern data8_t *galaxian_bulletsram;
extern data8_t *rockclim_videoram;

extern size_t galaxian_spriteram_size;
extern size_t galaxian_spriteram2_size;
extern size_t galaxian_bulletsram_size;

PALETTE_INIT( galaxian );
PALETTE_INIT( scramble );
PALETTE_INIT( turtles );
PALETTE_INIT( moonwar );
PALETTE_INIT( darkplnt );
PALETTE_INIT( rescue );
PALETTE_INIT( minefld );
PALETTE_INIT( stratgyx );
PALETTE_INIT( mariner );
PALETTE_INIT( frogger );
PALETTE_INIT( rockclim );






VIDEO_START( galaxian_plain );
VIDEO_START( galaxian );
VIDEO_START( gmgalax );
VIDEO_START( mooncrst );
VIDEO_START( mooncrgx );
VIDEO_START( moonqsr );
VIDEO_START( mshuttle );
VIDEO_START( pisces );
VIDEO_START( gteikob2 );
VIDEO_START( batman2 );
VIDEO_START( jumpbug );
VIDEO_START( dkongjrm );
VIDEO_START( scramble );
VIDEO_START( theend );
VIDEO_START( darkplnt );
VIDEO_START( rescue );
VIDEO_START( minefld );
VIDEO_START( calipso );
VIDEO_START( stratgyx );
VIDEO_START( mimonkey );
VIDEO_START( turtles );
VIDEO_START( frogger );
VIDEO_START( froggrmc );
VIDEO_START( mariner );
VIDEO_START( ckongs );
VIDEO_START( froggers );
VIDEO_START( newsin7 );
VIDEO_START( sfx );
VIDEO_START( rockclim );
VIDEO_START( drivfrcg );

VIDEO_UPDATE( galaxian );



/* defined in machine/scramble.c */
DRIVER_INIT( pisces );
DRIVER_INIT( checkmaj );
DRIVER_INIT( dingo );
DRIVER_INIT( kingball );
DRIVER_INIT( mooncrsu );
DRIVER_INIT( mooncrst );
DRIVER_INIT( mooncrgx );
DRIVER_INIT( moonqsr );
DRIVER_INIT( checkman );
DRIVER_INIT( gteikob2 );
DRIVER_INIT( azurian );
DRIVER_INIT( 4in1 );
DRIVER_INIT( mshuttle );
DRIVER_INIT( scramble_ppi );
DRIVER_INIT( amidar );
DRIVER_INIT( frogger );
DRIVER_INIT( froggers );
DRIVER_INIT( scobra );
DRIVER_INIT( stratgyx );
DRIVER_INIT( moonwar );
DRIVER_INIT( darkplnt );
DRIVER_INIT( tazmani2 );
DRIVER_INIT( anteater );
DRIVER_INIT( rescue );
DRIVER_INIT( minefld );
DRIVER_INIT( losttomb );
DRIVER_INIT( superbon );
DRIVER_INIT( hustler );
DRIVER_INIT( billiard );
DRIVER_INIT( mimonkey );
DRIVER_INIT( mimonsco );
DRIVER_INIT( atlantis );
DRIVER_INIT( scramble );
DRIVER_INIT( scrambls );
DRIVER_INIT( theend );
DRIVER_INIT( ckongs );
DRIVER_INIT( mariner );
DRIVER_INIT( mars );
DRIVER_INIT( devilfsh );
DRIVER_INIT( hotshock );
DRIVER_INIT( cavelon );
DRIVER_INIT( mrkougar );
DRIVER_INIT( mrkougb );
DRIVER_INIT( mimonscr );
DRIVER_INIT( sfx );
DRIVER_INIT( gmgalax );
DRIVER_INIT( ladybugg );

MACHINE_INIT( scramble );
MACHINE_INIT( sfx );
MACHINE_INIT( explorer );
MACHINE_INIT( galaxian );
MACHINE_INIT( devilfsg );

READ_HANDLER(scobra_type2_ppi8255_0_r);
READ_HANDLER(scobra_type2_ppi8255_1_r);
WRITE_HANDLER(scobra_type2_ppi8255_0_w);
WRITE_HANDLER(scobra_type2_ppi8255_1_w);

READ_HANDLER(hustler_ppi8255_0_r);
READ_HANDLER(hustler_ppi8255_1_r);
WRITE_HANDLER(hustler_ppi8255_0_w);
WRITE_HANDLER(hustler_ppi8255_1_w);

READ_HANDLER(amidar_ppi8255_0_r);
READ_HANDLER(amidar_ppi8255_1_r);
WRITE_HANDLER(amidar_ppi8255_0_w);
WRITE_HANDLER(amidar_ppi8255_1_w);

READ_HANDLER(frogger_ppi8255_0_r);
READ_HANDLER(frogger_ppi8255_1_r);
WRITE_HANDLER(frogger_ppi8255_0_w);
WRITE_HANDLER(frogger_ppi8255_1_w);

READ_HANDLER(mars_ppi8255_0_r);
READ_HANDLER(mars_ppi8255_1_r);
WRITE_HANDLER(mars_ppi8255_0_w);
WRITE_HANDLER(mars_ppi8255_1_w);


#define galaxian_coin_counter_0_w galaxian_coin_counter_w







INTERRUPT_GEN( hunchbks_vh_interrupt );
INTERRUPT_GEN( gmgalax_vh_interrupt );



/* defined in sndhrdw/galaxian.c */
extern struct CustomSound_interface galaxian_custom_interface;


/* defined in sndhrdw/scramble.c */




