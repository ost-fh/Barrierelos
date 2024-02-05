import {useTranslation} from "react-i18next";
import {useEffect, useRef, useState} from "react";
import {scrollToBottom} from "../../util/formatter.tsx";
import {Box, FormControl, InputLabel, MenuItem, Select, Typography} from "@mui/material";
import ArrowCircleDownIcon from "@mui/icons-material/ArrowCircleDown";
import {ParseKeys} from "i18next";
import Done from "./Done.tsx";
import {WebsiteMessage} from "../../lib/api-client";
import {categories} from "../../model/Categories.ts";

export default function ChooseCategory(props: {url: string}) {
  const { url } = props;
  const {t} = useTranslation();
  const endRef = useRef<null | HTMLDivElement>(null);
  const [category, setCategory] = useState<WebsiteMessage.category | string>("");

  useEffect(() => {
    setCategory("");
    scrollToBottom(endRef);
  }, [url]);

  const onCategory = (category: string) => {
    if(category !== "") {
      setCategory(category);
    }
  }

  return (
    <>
      <Box ref={endRef} display="flex" flexDirection="column" justifyContent="center" alignItems="center">
        <ArrowCircleDownIcon color={"disabled"} sx={{mt: 4, fontSize: 80}}/>
        <Typography component="h2" variant="h5" sx={{mt: 2}}>
          {t("ContributePage.ChooseCategory.title")}
        </Typography>
        <Typography component="p" variant="body1" sx={{my: 2}}>
          {t("ContributePage.ChooseCategory.text")}
        </Typography>
        <Box display="flex" gap="16px" sx={{flexGrow: 1, width: "100%", maxWidth: "320px"}}>
          <FormControl fullWidth={true}>
            <InputLabel id="label-category">{t("ContributePage.ChooseCategory.categoryLabel")}</InputLabel>
            <Select
              id="category"
              labelId="label-category"
              value={category}
              label={t("ContributePage.ChooseCategory.categoryLabel")}
              onChange={(event) => onCategory(event.target.value)}
            >
              {Object.values(categories).map(category =>
                <MenuItem key={category.category} value={category.category}>{t(category.translation as ParseKeys)}</MenuItem>
              )}
            </Select>
          </FormControl>
        </Box>
      </Box>
      {category !== "" && (
        <Done
          url={url}
          category={category as WebsiteMessage.category}
          tags={[]}
        />
      )}
    </>
  )
}
