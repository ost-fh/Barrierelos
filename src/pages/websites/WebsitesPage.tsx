import {ResultWebsite, Website, WebsiteControllerService} from "../../lib/api-client";
import {useTranslation} from "react-i18next";
import {Helmet} from "react-helmet-async";
import {DataGrid, GridColDef, GridPaginationModel, GridSortModel} from "@mui/x-data-grid";
import "./WebsitesPage.css"
import {formatScore} from "../../util/formatter.ts";
import {Box} from "@mui/material";
import {useEffect, useState} from "react";
import useSWRMutation from "swr/mutation";
import {Link} from "react-router-dom";
import {t} from "i18next";


function WebsitePage() {
  const {t} = useTranslation();

  const [rows, setRows] = useState([] as TableRow[]);
  const [paginationModel, setPaginationModel] = useState({
    page: 0,
    pageSize: 10,
  } as GridPaginationModel);
  const [sortModel, setSortModel] = useState([{
    field: "score",
    sort: "desc"
  }] as GridSortModel)

  const {
    data: websitesPage,
    error,
    isMutating: isLoading,
    trigger
  } = useSWRMutation<ResultWebsite, Error>("websites", getWebsites)
  useEffect(() => {
    void trigger()
  }, [paginationModel, sortModel])

  if (error) return `Error occurred:\n${error}`

  const [rowCount, setRowCount] = useState(
    websitesPage?.count || 20,
  );
  useEffect(() => {
    setRowCount((prevRowCount) =>
      websitesPage?.count !== undefined
        ? websitesPage.count
        : prevRowCount,
    );
  }, [websitesPage?.count, setRowCount]);

  return (
    <>
      <Helmet>
        <title>{t("WebsitesPage.title")} - {t("General.title")}</title>
      </Helmet>
      <h1>{t("WebsitesPage.title")}</h1>
      <Box sx={{width: "100%", maxWidth: 800}}>
        <DataGrid
          rows={rows}
          columns={columns}
          rowCount={rowCount}
          loading={isLoading}
          paginationModel={paginationModel}
          pageSizeOptions={[10]}
          disableRowSelectionOnClick
          disableColumnMenu
          pagination
          sortingMode="server"
          onSortModelChange={setSortModel}
          paginationMode="server"
          onPaginationModelChange={setPaginationModel}
        />
      </Box>
    </>
  )

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  async function getWebsites(_: string) {
    const websitePage = await WebsiteControllerService.getWebsites(
      paginationModel.page,
      paginationModel.pageSize,
      sortModel[0]?.field,
      sortModel[0]?.sort?.toUpperCase() as "ASC" | "DESC",
    )
    const websites = websitePage.content
    setRows(websites?.map((website) => ({
      id: website.id,
      domain: website.domain,
      score: formatScore(website.score),
      category: website.category,
      tags: website.tags,
    }) as TableRow) || [])
    setRowCount(websitePage.count)

    return websitePage
  }
}

const columns: GridColDef[] = [
  {
    field: "domain",
    headerName: "Domain",
    cellClassName: "website-domain-cell",
    flex: 50,
    renderCell: (params) => (
      <Link to={`/websites/${params.id}`}>{params.value}</Link>
    )
  },
  {
    field: "category",
    headerName: "Category",
    flex: 50,
    // @ts-expect-error: Interface is automatically generated from the translation file and consists of many union types
    valueFormatter: (params) => t(`WebsiteCategories.${params.value}`),
  },
  {
    field: "score",
    headerName: "Score",
    width: 80,
  },
];

interface TableRow {
  id: number
  domain: string
  category: Website.category
  score: string
}

export default WebsitePage
