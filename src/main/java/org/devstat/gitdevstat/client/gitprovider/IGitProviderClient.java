/* OpenSource 2023 */
package org.devstat.gitdevstat.client.gitprovider;

import java.util.List;
import org.devstat.gitdevstat.client.gitprovider.dto.RepositoryDto;

public interface IGitProviderClient {
    List<RepositoryDto> getRepositoryList(String teamSlug);
}
