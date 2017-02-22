package com.edasaki.core.shield;

import com.edasaki.core.AbstractManagerCore;
import com.edasaki.core.SakiCore;

public class ShieldManager extends AbstractManagerCore {

    private SakiShield activeShield = null;

    public ShieldManager(SakiCore plugin) {
        super(plugin);
    }

    @Override
    public void initialize() {
        register(new SakiShieldCore());
    }

    public void register(SakiShield shield) {
        if (this.activeShield != null) {
            activeShield.halt();
        }
        activeShield = shield;
        shield.start();
    }

}
