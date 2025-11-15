package dev.osunolimits.plugins.events.clans;

import dev.osunolimits.models.Clan;
import dev.osunolimits.plugins.ShiinaEventListener;
import dev.osunolimits.plugins.events.ShiinaEvent;
import lombok.Getter;

@Getter
public class OnUserLeaveClanEvent extends ShiinaEvent {

    private Clan clan;
    private int userId;

    public OnUserLeaveClanEvent(Clan clan, int userId) {
        this.clan = clan;
        this.userId = userId;
    }

    @Override
    public void call(ShiinaEventListener listener) {
        listener.onUserLeaveClanEvent(this);
    }
    
    
}
