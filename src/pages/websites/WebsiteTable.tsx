import {
  DataGrid,
  GridColDef,
  GridColumnVisibilityModel,
  GridPaginationModel,
  GridSortModel,
  GridValueFormatterParams
} from "@mui/x-data-grid";
import {useEffect, useState} from "react";
import useSWRMutation from "swr/mutation";
import {ResultWebsite, Website, WebsiteControllerService} from "../../lib/api-client";
import {Link} from "react-router-dom";
import {formatScore, translate} from "../../util/formatter.ts";
import LoadingIndicator from "../../components/LoadingIndicator.tsx";
import {useTranslation} from "react-i18next";
import {useMediaQuery, useTheme} from "@mui/material";

export function WebsiteTable() {
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

  const [columnVisibilityModel] = useState({
    category: true,
  } as GridColumnVisibilityModel)
  const theme = useTheme();
  columnVisibilityModel.category = useMediaQuery(theme.breakpoints.up("sm"));

  const {
    data: websitesPage,
    error,
    isMutating: isLoading,
    trigger
  } = useSWRMutation<ResultWebsite, Error>("websites", getWebsites)
  useEffect(() => {
    void trigger()
  }, [paginationModel, sortModel])

  const [rowCount, setRowCount] = useState(
    websitesPage?.totalElements || 20,
  );
  useEffect(() => {
    setRowCount((prevRowCount) =>
      websitesPage?.totalElements !== undefined
        ? websitesPage.totalElements
        : prevRowCount,
    );
  }, [websitesPage?.totalElements, setRowCount]);

  if (error) return `Error occurred:\n${error}`
  if (isLoading) return (<LoadingIndicator/>)

  const columns: GridColDef[] = [
    {
      field: "domain",
      headerName: t("WebsitesPage.WebsiteTable.domainHeaderLabel"),
      cellClassName: "website-domain-cell",
      flex: 50,
      renderCell: (params) => (
        <Link to={`/websites/${params.id}`}>{params.value}</Link>
      )
    },
    {
      field: "category",
      headerName: t("WebsitesPage.WebsiteTable.categoryHeaderLabel"),
      flex: 50,
      valueFormatter: (params: GridValueFormatterParams<Website.category>) => translate(t, `WebsiteCategories.${params.value.toString()}`),
    },
    {
      field: "score",
      headerName: t("WebsitesPage.WebsiteTable.scoreHeaderLabel"),
      width: 160,
      align: "center"
    },
  ];

  return (
    <DataGrid
      rows={rows}
      columns={columns}
      rowCount={rowCount}
      loading={isLoading}
      disableRowSelectionOnClick
      disableColumnMenu
      sortingMode="server"
      sortModel={sortModel}
      onSortModelChange={setSortModel}
      paginationMode="server"
      onPaginationModelChange={setPaginationModel}
      paginationModel={paginationModel}
      pageSizeOptions={[10]}
      columnVisibilityModel={columnVisibilityModel}
      aria-label={t("WebsitesPage.WebsiteTable.ariaLabel")}
    />
  )

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  async function getWebsites(_: string) {
    const websitePage = await WebsiteControllerService.getWebsites(
      false,
      false,
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
    setRowCount(websitePage.totalElements)

    return websitePage
  }
}

interface TableRow {
  id: number
  domain: string
  category: Website.category
  score: string
}
