package com.newfivefour.githubevents.logique;

import android.util.Log;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class Actions {

  public static class SettingsAction extends Action<Boolean> {
    public SettingsAction(boolean show) {
      super("SETTINGS", show);
    }
    public static Observable<Action> reactNoSettings() {
      return actionSent.filter(new Func1<Action, Boolean>() {
        @Override
        public Boolean call(Action action) {
          return action instanceof SettingsAction && !((SettingsAction)action).object;
        }
      });
    }
    public static Observable<Action> react() {
      return actionSent.filter(new Func1<Action, Boolean>() {
        @Override
        public Boolean call(Action action) {
          return action instanceof SettingsAction && ((SettingsAction)action).object;
        }
      });
    }
  }

  public static class ServerUpdateAction extends Action<String> {
    public ServerUpdateAction(String username) {
      super("USERNAMEUPDATE", username);
    }
    public static Observable<String> react() {
      return actionSent.filter(new Func1<Action, Boolean>() {
        @Override
        public Boolean call(Action action) {
          return action instanceof ServerUpdateAction;
        }
      })
      .flatMap(new Func1<Action, Observable<String>>() {
        @Override
        public Observable<String> call(Action action) {
          Log.d("HIYA", "flatmap Action: ");
          return Observable.just(((ServerUpdateAction)action).object);
        }
      });
    }
  }

  private static ActionsCallback callback;
  public static Observable<Action> actionSent = Observable.create(new ActionsObservable());

  public static void refresh() {
    callback.sendAction(new ServerUpdateAction(AppState.appState.getTitle()));
  }

  public static void changeUsername(String username) {
    callback.sendAction(new ServerUpdateAction(username));
  }

  public static void settings(boolean show) {
    callback.sendAction(new SettingsAction(show));
  }

  public static class ActionsObservable implements  Observable.OnSubscribe<Action> {
    ArrayList<Subscriber<? super Action>> subscribers = new ArrayList<>();

    public ActionsObservable() {
      Actions.callback = new ActionsCallback() {
        @Override
        public void sendAction(Action message) {
          Log.d("HIYA", "Action: " + message);
          for (Subscriber<? super Action> sub : subscribers) {
            sub.onNext(message);
          }
        }
      };
    }

    @Override
    public void call(Subscriber<? super Action> subscriber) {
      subscribers.add(subscriber);
    }
  }

  public abstract static class Action<T> {
    public final T object;
    public final String name;
    public Action(String name, T o) {
      this.name = name;
      this.object = o;
    }
  }

  public interface ActionsCallback {
    void sendAction(Action message);
  }
}