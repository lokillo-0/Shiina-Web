package dev.osunolimits.plugins.events.actions;

import dev.osunolimits.plugins.ShiinaEventListener;
import dev.osunolimits.plugins.events.ShiinaEvent;
import dev.osunolimits.utils.Auth;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OnGfxDeletionEvent extends ShiinaEvent {
    private String type;
    private Auth.User user;

    @Override
    public void call(ShiinaEventListener listener) {
        listener.onGfxDeletionEvent(this);
    }
}
