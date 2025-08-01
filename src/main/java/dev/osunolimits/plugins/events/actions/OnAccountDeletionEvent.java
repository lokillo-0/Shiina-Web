package dev.osunolimits.plugins.events.actions;

import dev.osunolimits.models.UserInfoObject;
import dev.osunolimits.plugins.ShiinaEventListener;
import dev.osunolimits.plugins.events.ShiinaEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OnAccountDeletionEvent extends ShiinaEvent {
    private UserInfoObject user;    

    @Override
    public void call(ShiinaEventListener listener) {
        listener.onAccountDeletionEvent(this);
    }
}
