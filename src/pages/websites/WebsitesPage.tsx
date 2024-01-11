import {useTranslation} from "react-i18next";
import {Helmet} from "react-helmet-async";
import "./WebsitesPage.css"
import {
  Box,
  Button,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  Stack,
} from "@mui/material";
import React, {useState} from "react";
import {WebsiteTable} from "./WebsiteTable.tsx";
import {RegionTable} from "./RegionTable.tsx";
import {useNavigate} from "react-router-dom";

function WebsitePage() {
  const {t} = useTranslation();
  const navigate = useNavigate();

  const [groupBy, setGroupBy] = useState(GroupBy.website);

  const onAddWebsite = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault();

    navigate('/contribute');
  }

  return (
    <>
      <Helmet>
        <title>{t("WebsitesPage.title")} - {t("General.title")}</title>
      </Helmet>
      <Box display="flex" flexDirection="row" justifyContent="space-between" alignItems="center">
        <h1>{t("WebsitesPage.title")}</h1>
        <Button onClick={onAddWebsite} variant="contained">{t("WebsitesPage.addWebsiteButton")}</Button>
      </Box>
      <Stack spacing={2}>
        <FormControl>
          <InputLabel id="group-by-select">{t("WebsitesPage.groupByLabel")}</InputLabel>
          <Select
            sx={{width: 200}}
            labelId="group-by-select"
            value={groupBy}
            label={t("WebsitesPage.groupByLabel")}
            onChange={(event: SelectChangeEvent) => setGroupBy(event.target.value as GroupBy)}
          >
            {Object.values(GroupBy).map((groupBy) => (
              <MenuItem key={groupBy} value={groupBy}>{t(`WebsitesPage.groupBy.${groupBy}`)}</MenuItem>
            ))}
          </Select>
        </FormControl>
        <Box sx={{width: "100%", maxWidth: 800}}>
          {groupBy === GroupBy.region ? <RegionTable/> : <WebsiteTable/>}
        </Box>
      </Stack>
    </>
  )
}

enum GroupBy {
  website = "website",
  region = "region",
}

export default WebsitePage;
