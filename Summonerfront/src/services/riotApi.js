const BASE_URL = "https://ddragon.leagueoflegends.com";

export async function getChampionIcon(champion) {
    const response = await fetch(`${BASE_URL}/api/versions.json`);
    const versions = await response.json();

    const version = versions[0];

    return `${BASE_URL}/cdn/${version}/img/champion/${champion}.png`;
}