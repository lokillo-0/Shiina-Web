package dev.osunolimits.plugins.events.actions;

import dev.osunolimits.plugins.ShiinaEventListener;
import dev.osunolimits.plugins.events.ShiinaEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OnShiinaSettingChangedEvent extends ShiinaEvent {
    private int adminId;
    private String key;
    private String value;

    @Override
    public void call(ShiinaEventListener listener) {
        listener.onShiinaSettingChangedEvent(this);
    }
}
