import {useTranslation} from "react-i18next";
import {Helmet} from "react-helmet-async";
import {Autocomplete, Button, Grid, Stack, TextField} from "@mui/material";
import {SyntheticEvent, useEffect, useState} from "react";
import {Website, WebsiteControllerService} from "../lib/api-client";
import WebsiteIcon from "@mui/icons-material/Language";
import {Link} from "react-router-dom";

function HomePage() {
  const {t} = useTranslation();

  const [websiteOptions, setWebsiteOptions] = useState([] as Website[]);
  const [selectedWebsite, setSelectedWebsite] = useState(null as Website | null);
  const [searchInput, setSearchInput] = useState("" as string);

  useEffect(() => {
    if (searchInput.length === 0) {
      setWebsiteOptions([]);
      return
    }
    const fetch = async () => {
      const result = await WebsiteControllerService.searchWebsite(searchInput)
      setWebsiteOptions(result);
    };
    void fetch();
  }, [searchInput]);

  useEffect(() => {
    if (selectedWebsite !== null) {
      document.getElementById("view-website-btn")?.focus()
    }
  }, [selectedWebsite]);

  return (
    <>
      <Helmet>
        <title>{t("HomePage.title")} - {t("General.title")}</title>
      </Helmet>
      <h1>{t("General.title")}</h1>

      <Stack direction={"row"} spacing={2}>
        <Autocomplete
          sx={{width: 300}}
          getOptionLabel={(website: Website) => website.domain}
          isOptionEqualToValue={(option: Website, value: Website) => option.id === value.id}
          filterOptions={(x) => x}
          options={websiteOptions}
          autoComplete
          includeInputInList
          filterSelectedOptions
          value={selectedWebsite}
          noOptionsText={searchInput.length === 0 ?
            t("HomePage.searchStringEmptyLabel") :
            t("HomePage.searchNoResultsLabel")}
          onChange={(_: SyntheticEvent, newValue: Website | null) => {
            setSelectedWebsite(newValue);
          }}
          onInputChange={(_: SyntheticEvent, newSearchInput) => {
            setSearchInput(newSearchInput);
          }}
          renderInput={(params) => (
            <TextField {...params} label={t("HomePage.searchLabel")} fullWidth/>
          )}
          renderOption={(props, website) => {
            return (
              <li {...props}>
                <Grid container alignItems="center">
                  <Grid item sx={{display: "flex", width: 44}}>
                    <WebsiteIcon sx={{color: "text.secondary"}}/>
                  </Grid>
                  <Grid item sx={{width: "calc(100% - 44px)", wordWrap: "break-word"}}>
                    {website.domain}
                  </Grid>
                </Grid>
              </li>
            );
          }}
        />
        <Button id="view-website-btn" component={Link} variant="outlined" disabled={selectedWebsite === null}
                to={selectedWebsite === null ? "/websites/" : `/websites/${selectedWebsite.id}`}>
          View Website
        </Button>
      </Stack>
    </>
  )
}

export default HomePage
