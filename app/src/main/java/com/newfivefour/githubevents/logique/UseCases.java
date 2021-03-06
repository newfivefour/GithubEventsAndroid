package com.newfivefour.githubevents.logique;

import rx.Observable;
import rx.functions.Action1;
import rx.observables.ConnectableObservable;

public class UseCases {

  static Action1<AppState> emptySubscribe = new Action1<AppState>() {
    @Override public void call(AppState appState) {}
  };

  public static void init() {

    // Actions observables
    Observable<AppState> settingsOn = Actions.actionSent.filter(ActionsFiltersMaps.settingsOn).flatMap(ActionsFiltersMaps.justState);
    Observable<AppState> settingsOff = Actions.actionSent.filter(ActionsFiltersMaps.settingsOff).flatMap(ActionsFiltersMaps.justState);
    Observable<AppState> serverUpdateNoContent = Actions.actionSent.filter(ActionsFiltersMaps.serverUpdateIfNoContent).flatMap(ActionsFiltersMaps.extractUsername).filter(AppStateFilters.areNoEvents);
    Observable<AppState> serverUpdateUsername = Actions.actionSent.filter(ActionsFiltersMaps.serverUpdateUsername).flatMap(ActionsFiltersMaps.extractUsernameInSettings);
    Observable<AppState> serverUpdate = Actions.actionSent.filter(ActionsFiltersMaps.serverUpdate).flatMap(ActionsFiltersMaps.extractUsername);
    Observable<AppState> updateRequest = Observable.merge(serverUpdateNoContent, serverUpdate, serverUpdateUsername);
    // Server observable list
    ConnectableObservable<AppState> serverRefresh = updateRequest.flatMap(Server.zippedEventsUsers()).publish();
    Observable<AppState> serverRefreshParsed = serverRefresh.filter(AppStateFilters.noException);
    Observable<AppState> serverRefreshFail = serverRefresh.filter(AppStateFilters.hasException);
    Observable<AppState> serverFailNoContent = serverRefreshFail.filter(AppStateFilters.areNoEvents);
    Observable<AppState> serverFailContent = serverRefreshFail.filter(AppStateFilters.areEvents);
    Observable<AppState> serverFailInSettings = serverRefreshFail.filter(AppStateFilters.hasShownSettings);
    Observable<AppState> serverFailNoSettingsWContent = serverFailContent.filter(AppStateFilters.hasNoShownSettings);
    Observable<AppState> noNetConnContentNoSettings = serverRefresh.filter(AppStateFilters.noInternetConnection).filter(AppStateFilters.areEvents).filter(AppStateFilters.hasNoShownSettings);

    // Subscriptions
    // Start loading and clear errors on service load
    updateRequest
    .map(AppStateMaps.setExceptionOffMap)
    .map(AppStateMaps.setLoadingOnMap)
    .map(AppStateMaps.setErrorOffInSettings)
    .map(AppStateMaps.setErrorOffMap)
    .map(AppStateMaps.setPopupErrorOffMap)
    .subscribe(emptySubscribe);
    // Stop loading page after a server refresh
    serverRefresh
    .map(AppStateMaps.setLoadingOffMap)
    .subscribe(emptySubscribe);
    // Parse good server return
    serverRefreshParsed
    .subscribe(emptySubscribe);
    // Show page error when no content
    serverFailNoContent
    .map(AppStateMaps.setErrorOnMap)
    .subscribe(emptySubscribe);
    // Popup error when content
    serverFailNoSettingsWContent
    .map(AppStateMaps.setPopupErrorOnMap)
    .subscribe(emptySubscribe);
    // Show settings
    settingsOn
    .map(AppStateMaps.setSettingsOnMap)
    .subscribe(emptySubscribe);
    // Dismiss settings
    settingsOff
    .map(AppStateMaps.setSettingsOffMap)
    .map(AppStateMaps.setErrorOffInSettings)
    .subscribe(emptySubscribe);
    // Dismiss settings box on good parse
    serverRefreshParsed
    .map(AppStateMaps.setSettingsOffMap)
    .subscribe(emptySubscribe);
    // Show error in editttext when refresh in settings
    serverFailInSettings
    .map(AppStateMaps.setErrorInSettings)
    .subscribe(emptySubscribe);
    // Show no connection error
    noNetConnContentNoSettings
    .map(AppStateMaps.setNoConnectionPopup)
    .subscribe(emptySubscribe);

    // TODO: Start learning about widgets
    // TODO: Create one and pubilsh to store

    serverRefresh.connect();
  }

}
