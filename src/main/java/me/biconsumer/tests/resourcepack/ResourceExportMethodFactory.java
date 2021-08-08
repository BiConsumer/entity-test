package me.biconsumer.tests.resourcepack;

import team.unnamed.hephaestus.resourcepack.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Factory for creating exporting methods
 * from {@link String} (from configuration)
 */
public final class ResourceExportMethodFactory {

    private ResourceExportMethodFactory() {
        throw new UnsupportedOperationException("Cannot instantiate this class!");
    }

    public static ResourceExporter<?> createExporter(
            String namespace,
            ResourcePackInfo info,
            File folder,
            String format
    ) throws IOException {

        String[] args = format.split(":");
        String method = args[0].toLowerCase();

        ResourcePackWriter writer = new ZipResourcePackWriter(namespace, info);

        switch (method) {
            case "mergezipfile":
            case "file": {
                if (args.length < 2) {
                    throw new IllegalArgumentException(
                            "Invalid format for file export: '" + format
                                    + "'. Use: 'file:filename'"
                    );
                }

                String filename = String.join(":", Arrays.copyOfRange(args, 1, args.length));
                return ResourceExports.newFileExporter(new File(folder, filename))
                        .setWriter(writer)
                        .setMergeZip(method.equals("mergezipfile"));
            }
            case "upload": {
                if (args.length < 3) {
                    throw new IllegalArgumentException(
                            "Invalid format for upload export: '" + format
                                    + "'. Use: 'upload:authorization:url'"
                    );
                }
                String authorization = args[1];
                String url = String.join(":", Arrays.copyOfRange(args, 2, args.length));

                if (authorization.equalsIgnoreCase("none")) {
                    authorization = null;
                }

                return ResourceExports.newHttpExporter(url)
                        .setWriter(writer)
                        .setAuthorization(authorization);
            }
            default: {
                throw new IllegalArgumentException(
                        "Invalid format: '" + format + "', unknown export"
                                + "method: '" + method + "'"
                );
            }
        }
    }
}