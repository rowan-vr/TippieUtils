/*
 * This a modified version adopted of https://github.com/LuckPerms/LuckPerms/blob/04bb035a83af96d55e7dffea514c505c66ce0f54/common/src/main/java/me/lucko/luckperms/common/dependencies/
 * licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 */

package me.tippie.tippieutils.dependencies;

public class DependencyDownloadException extends Exception {

    public DependencyDownloadException() {

    }

    public DependencyDownloadException(String message) {
        super(message);
    }

    public DependencyDownloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DependencyDownloadException(Throwable cause) {
        super(cause);
    }
}
