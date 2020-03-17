package com.dimas.rumahmakan.ui.activity.routingActivity;

import com.dimas.rumahmakan.base.BaseContract;

public class RoutingActivityContract {
    public interface View extends BaseContract.View {
        // add more for response
    }

    public interface Presenter extends BaseContract.Presenter<View> {
        // add for request
    }
}
