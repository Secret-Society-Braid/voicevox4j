package org.braid.society.secret.voicevox4j.exception;

/**
 * Voicevox操作中に発生するエラーを表す例外クラス。
 */
public class VoicevoxException extends Exception {

    private final int resultCode;

    /**
     * エラーメッセージと結果コードを指定してVoicevoxExceptionを作成します。
     *
     * @param message エラーメッセージ
     * @param resultCode Voicevoxの結果コード
     */
    public VoicevoxException(String message, int resultCode) {
        super(message);
        this.resultCode = resultCode;
    }

    /**
     * エラーメッセージ、結果コード、原因を指定してVoicevoxExceptionを作成します。
     *
     * @param message エラーメッセージ
     * @param resultCode Voicevoxの結果コード
     * @param cause 原因となった例外
     */
    public VoicevoxException(String message, int resultCode, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
    }

    /**
     * Voicevoxの結果コードを取得します。
     *
     * @return 結果コード
     */
    public int getResultCode() {
        return resultCode;
    }
}
