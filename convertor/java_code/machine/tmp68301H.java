#ifndef TMP68301_H
#define TMP68301_H

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package machine;

public class tmp68301H
{
	
	// Machine init
	MACHINE_INIT( tmp68301 );
	
	// Hardware Registers
	extern data16_t *tmp68301_regs;
	WRITE16_HANDLER( tmp68301_regs_w );
	
	// Interrupts
	void tmp68301_external_interrupt_0(void);
	void tmp68301_external_interrupt_1(void);
	void tmp68301_external_interrupt_2(void);
	
	#endif
}
