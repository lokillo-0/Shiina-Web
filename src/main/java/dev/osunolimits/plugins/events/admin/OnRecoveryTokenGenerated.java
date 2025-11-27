package dev.osunolimits.plugins.events.admin;

import dev.osunolimits.plugins.ShiinaEventListener;
import dev.osunolimits.plugins.events.ShiinaEvent;
import lombok.AllArgsConstructor;

import lombok.Getter;

@Getter
@AllArgsConstructor
public class OnRecoveryTokenGenerated extends ShiinaEvent {
    private int userId;
    private String recoveryToken;

    @Override
    public void call(ShiinaEventListener listener) {
        listener.onRecoveryTokenGenerated(this);
    }
}
