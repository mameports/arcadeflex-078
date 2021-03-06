/* Variables needed by vidhrdw: */


/* Variables defined in vidhrdw: */

extern data16_t *unico_vram_0,   *unico_scrollx_0, *unico_scrolly_0;
extern data16_t *unico_vram_1,   *unico_scrollx_1, *unico_scrolly_1;
extern data16_t *unico_vram_2,   *unico_scrollx_2, *unico_scrolly_2;
extern data32_t *unico_vram32_0, *unico_vram32_1, *unico_vram32_2, *unico_scroll32;

/* Functions defined in vidhrdw: */

WRITE16_HANDLER( unico_vram_0_w );
WRITE16_HANDLER( unico_vram_1_w );
WRITE16_HANDLER( unico_vram_2_w );
WRITE16_HANDLER( unico_palette_w );

WRITE32_HANDLER( unico_vram32_0_w );
WRITE32_HANDLER( unico_vram32_1_w );
WRITE32_HANDLER( unico_vram32_2_w );
WRITE32_HANDLER( unico_palette32_w );

VIDEO_START( unico );
VIDEO_UPDATE( unico );

VIDEO_START( zeropnt2 );
VIDEO_UPDATE( zeropnt2 );

