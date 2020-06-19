package com.civrealms.plugin.common.rabbit;

public interface ConfirmListener {
  void success();
  void fail();

  static ConfirmListener create(Runnable success, Runnable fail) {
    return new ConfirmListener() {
      @Override
      public void success() {
        if (success != null) {
          try {
            success.run();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }

      @Override
      public void fail() {
        if (fail !=  null) {
          try {
            fail.run();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    };
  }
}
