#!/bin/bash
# This script is based on: https://serverfault.com/questions/1021883/how-to-delete-docker-images-older-than-x-days-from-docker-hub-using-a-shell-scri

set -e

# set username and password
UNAME=$docker_username
UPASS=$docker_password
REPO_LIST="regexgenerator"
KEEP=$(( 15 > numberOfImagesToKeep ? 15 : numberOfImagesToKeep ))

echo "Logging in..."
# get token to be able to talk to Docker Hub
TOKEN=$(curl -s -H "Content-Type: application/json" -X POST -d '{"username": "'${UNAME}'", "password": "'${UPASS}'"}' https://hub.docker.com/v2/users/login/ | jq -r .token)

echo
echo "Working on repository: ${REPO_LIST}"

for REPO in ${REPO_LIST}
do
  # get tags for REPO
  echo
  echo "Listing tags for repository ${UNAME}/${REPO}"
  IMAGE_TAGS=$(curl -s -H "Authorization: JWT ${TOKEN}" https://hub.docker.com/v2/repositories/${UNAME}/${REPO}/tags/?page_size=10000 | jq -r '.results|.[]|.name')

  # build a list of tags with timestamps for this REPO
  TAG_ARRAY=()
  for TAG in ${IMAGE_TAGS}
  do
    # add last_updated_time
    echo "Reading tag: $TAG"
    UPDATED_TIME=$(curl -s -H "Authorization: JWT ${TOKEN}" https://hub.docker.com/v2/repositories/${UNAME}/${REPO}/tags/${TAG}/?page_size=10000 | jq -r '.last_updated')
    TAG_ARRAY+=("${UPDATED_TIME} ${TAG}")
  done

  # sort array by date
  IFS=$'\n' TAG_ARRAY=($(sort <<<"${TAG_ARRAY[*]}")); unset IFS
  echo
  echo "Tags sorted chronologically"
  printf "  %s\n" "${TAG_ARRAY[@]}"

  # compute tags to be deleted
  COUNT=${#TAG_ARRAY[@]}
  REMOVE=$((COUNT - KEEP))

  if (( REMOVE > 0 )); then
    echo
    echo "Delete ${REMOVE} of the ${COUNT} image tags"

    # only keep forst REMOVE tags
    TAGS_TO_DELETE=("${TAG_ARRAY[@]:0:$REMOVE}")
    # adjust array items to only contain tags
    IFS=$'\n' TAGS_TO_DELETE=($(cut -d ' ' -f 2 <<<"${TAGS_TO_DELETE[*]}")); unset IFS

    echo
    echo "Tags to be deleted:"
    printf "  %s\n" "${TAGS_TO_DELETE[@]}"
    echo

    for TAG in "${TAGS_TO_DELETE[@]}"
    do
      echo "Deleting: ${TAG}"
      curl -s  -X DELETE  -H "Authorization: JWT ${TOKEN}" https://hub.docker.com/v2/repositories/${UNAME}/${REPO}/tags/${TAG}/
    done
  fi
done

echo
echo "Old tags deleted."

