package trios.linesales;

import okhttp3.Route;

public class RouteDetails {
    String routeCode, routeName;

    public RouteDetails (String routeCode, String routeName) {
        this.routeCode = routeCode;
        this.routeName = routeName;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public String getRouteName() {
        return routeName;
    }
}
