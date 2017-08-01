package org.gortz.greeniot.smartcityiot2.dto.listitems;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Container for license information.
 */

@Getter
@AllArgsConstructor(suppressConstructorProperties = true)
public class License {
    /*
    * Identifier of license.
    *
    * @return id
    * */
    private int id;

    /**
     * Name of licensed resource.
     *
     * @return Name of licensed resource.
     */
    private String name;

    /**
     * Website to licensed resource.
     *
     * @return Website
     */
    private String website;

    /**
     * The license text of the licensed resource.
     *
     * @return License text.
     */
    private String licenseText;
}
